package org.jvi.tools.orchestrator.configuration;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FilenameUtils;
import org.jvi.tools.orchestrator.model.ProcessConfiguration;
import org.jvi.tools.orchestrator.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultFileSystemEventProcessor implements FileSystemEventProcessor {

    @Autowired
    private final ProcessRepository processRepository;

    @Autowired
    private final ConfigurationDecoder configurationDecoder;

    @Override
    public void processCreation(final Path target) {
        this.processModification(target);
    }

    @Override
    public void processModification(final Path target) {
        try {
            processRepository.update(buildConfiguration(target));
        } catch (ConfigurationException | InvalidConfigurationException e) {
            log.error("Unable to process configuration for path: {}", target, e);
        }
    }

    @Override
    public void processDeletion(final Path target) {
        processRepository.remove(buildIdentifier(target));
    }

    private String buildIdentifier(final Path target) {
        return FilenameUtils.getBaseName(target.getFileName().toString());
    }

    private ProcessConfiguration buildConfiguration(final Path target) throws ConfigurationException {
        return configurationDecoder.decodeConfiguration(buildIdentifier(target),
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(
                        PropertiesConfiguration.class).configure(
                                new Parameters().properties().setFileName(
                                        target.toString())).getConfiguration());
    }

}
