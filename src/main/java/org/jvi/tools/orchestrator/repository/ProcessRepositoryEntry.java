package org.jvi.tools.orchestrator.repository;

import org.jvi.tools.orchestrator.engine.ProcessManager;
import org.jvi.tools.orchestrator.model.ProcessConfiguration;

import lombok.Data;

@Data
public class ProcessRepositoryEntry {

    private final ProcessConfiguration processConfiguration;

    private ProcessManager processManager;

}
