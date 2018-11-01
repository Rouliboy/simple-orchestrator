package org.jvi.tools.orchestrator.repository;

import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;
import static org.jvi.tools.orchestrator.repository.ProcessState.DISABLED;
import static org.jvi.tools.orchestrator.repository.ProcessState.ENABLED;

import org.jvi.tools.orchestrator.engine.ProcessLauncher;
import org.jvi.tools.orchestrator.engine.ProcessManager;
import org.jvi.tools.orchestrator.model.ProcessConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessRepository {

    @Autowired
    private final ProcessLauncher processLauncher;

    private final Map<String, ProcessRepositoryEntry> entries = new HashMap<String, ProcessRepositoryEntry>();

    @Synchronized
    public List<ProcessDescriptor> getProcessDescriptors() {
        return unmodifiableList(
                entries.values().stream().map(this::buildProcessDescriptor).collect(Collectors.toList()));
    }

    @Synchronized
    public ProcessDescriptor getProcessDescriptor(final String id) {
        return ofNullable(entries.get(id)).map(this::buildProcessDescriptor).orElseThrow(
                () -> new IllegalArgumentException("no process with id " + id + " found"));
    }

    @Synchronized
    public void enable(final String id) {
        log.info("enabling process: {}", id);
        doStart(ofNullable(entries.get(id)).orElseThrow(
                () -> new IllegalArgumentException("no process with id " + id + " found")));
    }

    @Synchronized
    public void disable(final String id) {
        log.info("disabling process: {}", id);
        doStop(ofNullable(entries.get(id)).orElseThrow(
                () -> new IllegalArgumentException("no process with id " + id + " found")));
    }

    @Synchronized
    public void remove(final String id) {
        log.info("removing process: {}", id);
        doStop(ofNullable(entries.remove(id)).orElseThrow(
                () -> new IllegalArgumentException("no process with id " + id + " found")));
    }

    @Synchronized
    public void update(final ProcessConfiguration processConfiguration) {
        log.info("updating process: {}", processConfiguration.getId());
        if (entries.containsKey(processConfiguration.getId())) {
            final ProcessRepositoryEntry oldEntry = entries.get(processConfiguration.getId());
            if (oldEntry.getProcessConfiguration().equals(processConfiguration)) {
                return;
            } else {
                ofNullable(oldEntry.getProcessManager()).ifPresent(ProcessManager::kill);
            }
        }
        final ProcessRepositoryEntry entry = new ProcessRepositoryEntry(processConfiguration);
        entry.setProcessManager(processLauncher.startProcess(processConfiguration));
        entries.put(processConfiguration.getId(), entry);
    }

    private ProcessDescriptor buildProcessDescriptor(final ProcessRepositoryEntry pre) {
        return new ProcessDescriptor(pre.getProcessConfiguration(),
                pre.getProcessManager() == null ? DISABLED : ENABLED);
    }

    private void doStart(final ProcessRepositoryEntry processRepositoryEntry) {
        if (processRepositoryEntry.getProcessManager() == null) {
            processRepositoryEntry.setProcessManager(
                    processLauncher.startProcess(processRepositoryEntry.getProcessConfiguration()));
        }
    }

    private void doStop(final ProcessRepositoryEntry processRepositoryEntry) {
        if (processRepositoryEntry.getProcessManager() != null) {
            processRepositoryEntry.getProcessManager().kill();
            processRepositoryEntry.setProcessManager(null);
        }
    }

}
