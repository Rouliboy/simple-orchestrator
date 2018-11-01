package org.jvi.tools.orchestrator.repository;

import org.jvi.tools.orchestrator.model.ProcessConfiguration;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProcessDescriptor {

    private final ProcessConfiguration processConfiguration;

    private final ProcessState processState;

}
