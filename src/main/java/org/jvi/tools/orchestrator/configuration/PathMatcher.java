package org.jvi.tools.orchestrator.configuration;

import java.nio.file.Path;

public interface PathMatcher {

    boolean match(Path path);

}
