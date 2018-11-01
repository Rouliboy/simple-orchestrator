package org.jvi.tools.orchestrator.engine;

import org.apache.commons.exec.ExecuteWatchdog;
import org.springframework.stereotype.Component;

@Component
public class DefaultExecuteWatchdogFactory implements ExecuteWatchdogFactory {

    @Override
    public ExecuteWatchdog build(final long timeout) {
        return new ExecuteWatchdog(timeout);
    }

}
