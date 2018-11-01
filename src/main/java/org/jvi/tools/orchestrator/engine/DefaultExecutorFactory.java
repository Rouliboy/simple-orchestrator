package org.jvi.tools.orchestrator.engine;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.springframework.stereotype.Component;

@Component
public class DefaultExecutorFactory implements ExecutorFactory {

    @Override
    public Executor build() {
        return new DefaultExecutor();
    }

}
