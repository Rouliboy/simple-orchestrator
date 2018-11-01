package org.jvi.tools.orchestrator.configuration;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import org.apache.commons.configuration2.Configuration;
import org.jvi.tools.orchestrator.model.ProcessConfiguration;
import org.jvi.tools.orchestrator.model.ProcessConfiguration.ProcessConfigurationBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ConfigurationDecoder {

    public static final String EQUALS = "=";

    public static final String ENVIRONMENT = "environment";

    public static final String ARGUMENT = "argument";

    public static final String TIMEOUT = "timeout";

    public static final String WORKING_DIRECTORY = "workingDirectory";

    public static final String EXECUTABLE = "executable";

    public static final String PRE_GROOVY_SCRIPT = "preGroovyScript";

    public static final String POST_GROOVY_SCRIPT = "postGroovyScript";

    public ProcessConfiguration decodeConfiguration(final String id, final Configuration config) {
        try {
            config.setProperty("configuration.id", id);
            final ProcessConfigurationBuilder builder = ProcessConfiguration.builder().id(id).executable(
                    config.getString(EXECUTABLE)).workingDirectory(
                            config.getString(WORKING_DIRECTORY)).preGroovyScripts(config.getList(String.class,
                                    PRE_GROOVY_SCRIPT, emptyList())).postGroovyScripts(
                                            config.getList(String.class, POST_GROOVY_SCRIPT, emptyList()));
            ofNullable(config.getString(TIMEOUT)).map(Duration::parse).ifPresent(builder::timeout);
            config.getList(String.class, ARGUMENT, emptyList()).stream().forEach(builder::argument);
            config.getList(String.class, ENVIRONMENT, emptyList()).stream().filter(
                    e -> e.indexOf(EQUALS) != 0).forEach(e -> {
                        if (e.contains(EQUALS)) {
                            builder.environment(e.substring(0, e.indexOf(EQUALS)),
                                    e.substring(e.indexOf(EQUALS) + 1, e.length()));
                        } else {
                            builder.environment(e, "");
                        }
                    });
            return builder.build();
        } catch (final Exception e) {
            throw new InvalidConfigurationException(e);
        }
    }

}
