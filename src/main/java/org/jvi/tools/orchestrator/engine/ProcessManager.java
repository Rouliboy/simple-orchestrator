package org.jvi.tools.orchestrator.engine;

import static java.util.Optional.ofNullable;
import static org.jvi.tools.orchestrator.engine.ProcessStatus.ERROR;
import static org.jvi.tools.orchestrator.engine.ProcessStatus.KILLED;
import static org.jvi.tools.orchestrator.engine.ProcessStatus.RUNNING;
import static org.jvi.tools.orchestrator.engine.ProcessStatus.STARTING;
import static org.jvi.tools.orchestrator.engine.ProcessStatus.STOPPED;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.util.MapUtils;
import org.jvi.tools.orchestrator.model.ProcessConfiguration;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessManager {

	@Getter
	private final ProcessConfiguration processConfiguration;

	private final OutputStream outputStream;

	private final ExecutorFactory executorFactory;

	@Getter
	private transient ProcessStatus status = STOPPED;

	private final ExecuteWatchdog watchDog;

	@Getter
	private Date lastStart;

	public ProcessManager(final ProcessConfiguration processConfiguration, final OutputStream outputStream,
			final ExecuteWatchdogFactory executeWatchdogFactory, final ExecutorFactory executorFactory) {
		super();
		this.processConfiguration = processConfiguration;
		this.outputStream = outputStream;
		this.executorFactory = executorFactory;
		if (processConfiguration.getTimeout().isZero() || processConfiguration.getTimeout().isNegative()) {
			watchDog = executeWatchdogFactory.build(ExecuteWatchdog.INFINITE_TIMEOUT);
		} else {
			watchDog = executeWatchdogFactory.build(processConfiguration.getTimeout().toMillis());
		}
	}

	public void execute() {
		lastStart = new Date();
		status = STARTING;
		try {
			startProcess();
			status = RUNNING;
		} catch (final Exception e) {
			log.error("an error occured while starting process", e);
			status = ERROR;
		}
	}

	public void kill() {
		try {
			watchDog.destroyProcess();
			executeGroovyScripts(processConfiguration.getPostGroovyScripts());
		} catch (final Exception e) {
			log.error("an error occured while killing process", e);
		} finally {
			status = KILLED;
		}
	}

	@Override
	public String toString() {
		return processConfiguration.getId();
	}

	private ExecuteResultHandler buildExecuteResultHandler() {
		return new ExecuteResultHandler() {
			@Override
			public void onProcessFailed(final ExecuteException e) {
				log.error("an error occured while executing executor", e);
				if (status != KILLED) {
					status = ERROR;
				}
				executeGroovyScripts(processConfiguration.getPostGroovyScripts());
			}

			@Override
			public void onProcessComplete(final int exitValue) {
				if (status != KILLED) {
					log.info("process completed with value {}", exitValue);
					status = STOPPED;
				}
				executeGroovyScripts(processConfiguration.getPostGroovyScripts());
			}
		};
	}

	private CommandLine buildCommandLine() {
		final CommandLine c = new CommandLine(processConfiguration.getExecutable());
		processConfiguration.getArguments().forEach(a -> c.addArguments(a, false));
		return c;
	}

	private void startProcess() throws Exception {
		executeGroovyScripts(processConfiguration.getPreGroovyScripts());
		final Executor executor = executorFactory.build();
		executor.setStreamHandler(new PumpStreamHandler(outputStream));
		executor.setWatchdog(watchDog);
		ofNullable(processConfiguration.getWorkingDirectory()).map(File::new).ifPresent(executor::setWorkingDirectory);
		executor.execute(buildCommandLine(), MapUtils.merge(System.getenv(), processConfiguration.getEnvironment()),
				buildExecuteResultHandler());
	}

	private void executeGroovyScripts(final List<String> groovyScripts) {
		for (final String groovyScript : groovyScripts) {
			try {
				if (groovyScript != null) {
					final Binding binding = new Binding();
					binding.setProperty("out", new PrintStream(outputStream));
					binding.setProperty("err", new PrintStream(outputStream));
					binding.setVariable("processConfiguration", processConfiguration);
					final CommandLine parsedCommandLine = CommandLine.parse(groovyScript);
					new GroovyShell(binding).run(new File(parsedCommandLine.getExecutable()),
							parsedCommandLine.getArguments());
				}
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
