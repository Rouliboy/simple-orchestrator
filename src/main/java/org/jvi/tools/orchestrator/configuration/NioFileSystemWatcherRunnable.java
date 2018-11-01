package org.jvi.tools.orchestrator.configuration;

import static java.lang.Thread.currentThread;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ToString
public class NioFileSystemWatcherRunnable implements Runnable {

    private final Path path;

    private final FileSystemEventProcessor fileSystemEventProcessor;

    @Override
    public void run() {
        log.info("Starting watching {}", path);
        while (!currentThread().isInterrupted()) {
            try {
                Files.walk(path).filter(Files::isRegularFile).forEach(
                        fileSystemEventProcessor::processModification);
                final WatchService watcher = FileSystems.getDefault().newWatchService();
                path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                for (final WatchKey key = watcher.take(); key.reset();) {
                    key.pollEvents().stream().filter(e -> e.kind() != OVERFLOW).forEach(e -> {
                        final Path target = path.resolve((Path) e.context());
                        if (ENTRY_CREATE.equals(e.kind())) {
                            fileSystemEventProcessor.processCreation(target);
                        } else if (ENTRY_MODIFY.equals(e.kind())) {
                            fileSystemEventProcessor.processModification(target);
                        } else if (ENTRY_DELETE.equals(e.kind())) {
                            fileSystemEventProcessor.processDeletion(target);
                        }
                    });
                    sleep();
                }
            } catch (final InterruptedException e) {
                currentThread().interrupt();
            } catch (final Exception e) {
                log.error("Error, retrying to watch {} after 1s", path, e);
                sleep();
            }
        }
        log.info("Stopped watching {}", path);
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            currentThread().interrupt();
        }
    }

}
