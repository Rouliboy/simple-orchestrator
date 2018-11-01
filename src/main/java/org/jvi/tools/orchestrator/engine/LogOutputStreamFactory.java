package org.jvi.tools.orchestrator.engine;

import org.apache.commons.exec.LogOutputStream;
import org.jvi.tools.orchestrator.model.ProcessConfiguration;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogOutputStreamFactory implements OutputStreamFactory {

    @Override
    public OutputStream build(final ProcessConfiguration processConfiguration) {
        return new LogOutputStream() {
            @Override
            protected void processLine(final String line, final int logLevel) {
                MDC.put("logFileName", processConfiguration.getId());
                log.info(line);
                MDC.remove("logFileName");
            }
        };
    }

}
