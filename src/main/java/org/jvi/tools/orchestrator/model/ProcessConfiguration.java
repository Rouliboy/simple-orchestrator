package org.jvi.tools.orchestrator.model;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Builder
@RequiredArgsConstructor
@Data
public class ProcessConfiguration {

    @NonNull
    private final String id;

    @NonNull
    private final String executable;
    
    @Singular
    private final List<String> preGroovyScripts;
    
    @Singular
    private final List<String> postGroovyScripts;

    @Singular
    private final List<String> arguments;

    @Singular("environment")
    private final Map<String, String> environment;

    private final String workingDirectory;

    private final Duration timeout;

    public static class ProcessConfigurationBuilder {
        private Duration timeout = Duration.ZERO;
    }

}
