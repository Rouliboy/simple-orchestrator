
package org.jvi.tools.orchestrator.configuration;

import static java.lang.Thread.currentThread;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ThreadRunner {

    private Thread thread;

    private final Runnable runnable;

    @Synchronized
    public void start() {
        log.debug("Starting {}", runnable);
        if (thread == null) {
            thread = new Thread(runnable);
            thread.start();
            log.info("Started {}", runnable);
        }
    }

    @Synchronized
    public void stop() {
        log.debug("Stopping {}", runnable);
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (final InterruptedException e) {
                currentThread().interrupt();
            } finally {
                thread = null;
            }
            log.info("Stopped {}", runnable);
        }
    }

}
