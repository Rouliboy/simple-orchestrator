package org.jvi.tools.orchestrator.engine;

import org.jvi.tools.orchestrator.model.ProcessConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DefaultProcessManagerFactory implements ProcessManagerFactory {

    @Autowired
    private final ExecuteWatchdogFactory executeWatchdogFactory;

    @Autowired
    private final ExecutorFactory executorFactory;

    @Override
    public ProcessManager build(final ProcessConfiguration processConfiguration,
            final OutputStream outputStream) {
        return new ProcessManager(processConfiguration, outputStream, executeWatchdogFactory,
                executorFactory);
    }

}
