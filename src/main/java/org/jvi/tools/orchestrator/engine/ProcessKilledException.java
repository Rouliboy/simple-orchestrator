package org.jvi.tools.orchestrator.engine;

public class ProcessKilledException extends RuntimeException {

    private static final long serialVersionUID = 3823132734433732561L;

    private final ProcessManager processManager;

    public ProcessKilledException(final ProcessManager processManager) {
        this.processManager = processManager;
    }

    @Override
    public String getMessage() {
        return "process killed: " + processManager.getProcessConfiguration();
    }

}
