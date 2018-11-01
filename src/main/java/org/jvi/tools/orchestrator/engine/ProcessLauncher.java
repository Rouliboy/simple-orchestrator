package org.jvi.tools.orchestrator.engine;

import org.jvi.tools.orchestrator.model.ProcessConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class ProcessLauncher {

    @Autowired
    private ScheduledExecutorService executorService;

    @Autowired
    private OutputStreamFactory outputStreamFactory;

    @Autowired
    private ProcessManagerFactory processManagerFactory;

    @Value("${secondsBetweenCheck:10}")
    private long secondsBetweenCheck;

    public ProcessManager startProcess(final ProcessConfiguration processConfiguration) {
        final ProcessManager processManager = processManagerFactory.build(processConfiguration,
                outputStreamFactory.build(processConfiguration));
        executorService.scheduleAtFixedRate(new ProcessMonitor(processManager), 0L, secondsBetweenCheck,
                TimeUnit.SECONDS);
        log.info("created process {}", processManager);
        return processManager;
    }

}
