package org.jvi.tools.orchestrator.configuration;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DefaultPathMatcher implements PathMatcher {

    @Override
    public boolean match(final Path path) {
        final String fileName = path.getFileName().toString();
        return fileName.endsWith(".properties") && !fileName.startsWith("_");
    }

}
