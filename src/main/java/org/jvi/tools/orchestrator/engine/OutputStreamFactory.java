package org.jvi.tools.orchestrator.engine;

import java.io.OutputStream;

import org.jvi.tools.orchestrator.model.ProcessConfiguration;

public interface OutputStreamFactory {

    OutputStream build(ProcessConfiguration processConfiguration);

}
