package org.jvi.tools.orchestrator.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import lombok.RequiredArgsConstructor;

@Component
@Primary
@RequiredArgsConstructor
public class FilteringFileSystemEventProcessor implements FileSystemEventProcessor {

    @Autowired
    private final PathMatcher pathMatcher;

    @Autowired
    private final FileSystemEventProcessor fileSystemEventProcessor;

    @Override
    public void processCreation(final Path target) {
        if (pathMatcher.match(target)) {
            fileSystemEventProcessor.processCreation(target);
        }
    }

    @Override
    public void processModification(final Path target) {
        if (pathMatcher.match(target)) {
            fileSystemEventProcessor.processModification(target);
        }
    }

    @Override
    public void processDeletion(final Path target) {
        if (pathMatcher.match(target)) {
            fileSystemEventProcessor.processDeletion(target);
        }
    }

}
