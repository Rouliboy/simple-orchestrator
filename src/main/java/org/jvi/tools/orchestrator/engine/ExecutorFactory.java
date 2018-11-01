package org.jvi.tools.orchestrator.engine;

import org.apache.commons.exec.Executor;

public interface ExecutorFactory {

    Executor build();

}
