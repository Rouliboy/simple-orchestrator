package org.jvi.tools.orchestrator.spring;

import org.jvi.tools.orchestrator.model.ProcessConfiguration;
import org.jvi.tools.orchestrator.repository.ProcessDescriptor;
import org.jvi.tools.orchestrator.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class Cleaner {

    @Autowired
    private ProcessRepository processRepository;

    @PreDestroy
    public void cleanup() {
        processRepository.getProcessDescriptors().stream().map(
                ProcessDescriptor::getProcessConfiguration).map(ProcessConfiguration::getId).forEach(
                        processRepository::disable);
    }

}
