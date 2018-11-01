package org.jvi.tools.orchestrator.configuration;

public class InvalidConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 5616978697734931482L;

    public InvalidConfigurationException(final Exception e) {
        super(e);
    }

}
