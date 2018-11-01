package org.jvi.tools.orchestrator.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ProcessMonitor implements Runnable {

    @Getter
    private final ProcessManager processManager;

    @Override
    public void run() {
        try {
            ProcessStatus status = processManager.getStatus();
            switch (status) {
                case STOPPED:
                case ERROR:
                    log.info("found {} in state {}, starting it", processManager, status);
                    processManager.execute();
                    break;

                case KILLED:
                    log.debug("found {} in state {}", processManager, status);
                    throw new ProcessKilledException(processManager);

                default:
                    log.debug("{} is in state {}", processManager, status);
                    break;
            }
        } catch (final ProcessKilledException e) {
            log.debug("process killed: {}", processManager, e);
            throw e;
        } catch (final Exception e) {
            log.error("error while monitoring process", e);
        }
    }

}
