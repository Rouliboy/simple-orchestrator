package org.jvi.tools.orchestrator.configuration;

import java.nio.file.Path;

public interface FileSystemEventProcessor {

    void processCreation(Path target);

    void processModification(Path target);

    void processDeletion(Path target);

}
