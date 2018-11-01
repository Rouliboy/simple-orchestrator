package org.jvi.tools.orchestrator.web;

import org.jvi.tools.orchestrator.repository.ProcessDescriptor;
import org.jvi.tools.orchestrator.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProcessRepositoryController {

    @Autowired
    private ProcessRepository processRepository;

    @RequestMapping("/processes")
    public @ResponseBody List<ProcessDescriptor> listProcess() {
        return processRepository.getProcessDescriptors();
    }

    @RequestMapping("/processes/{id}")
    public @ResponseBody ProcessDescriptor getProcess(@PathVariable
    final String id) {
        return processRepository.getProcessDescriptor(id);
    }

    @RequestMapping("/processes/{id}/enable")
    public ResponseEntity<Void> startProcess(@PathVariable
    final String id) {
        processRepository.enable(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping("/processes/{id}/disable")
    public ResponseEntity<Void> stopProcess(@PathVariable
    final String id) {
        processRepository.disable(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
