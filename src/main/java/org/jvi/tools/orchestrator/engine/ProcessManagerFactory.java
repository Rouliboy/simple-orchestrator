package org.jvi.tools.orchestrator.engine;

import java.io.OutputStream;

import org.jvi.tools.orchestrator.model.ProcessConfiguration;

public interface ProcessManagerFactory {

    ProcessManager build(ProcessConfiguration processConfiguration, OutputStream outputStream);

}
