package org.jvi.tools.orchestrator.engine;

import org.apache.commons.exec.ExecuteWatchdog;

public interface ExecuteWatchdogFactory {

    ExecuteWatchdog build(long timeout);

}
