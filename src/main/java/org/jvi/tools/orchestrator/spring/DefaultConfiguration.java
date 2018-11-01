package org.jvi.tools.orchestrator.spring;

import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jvi.tools.orchestrator.configuration.FileSystemEventProcessor;
import org.jvi.tools.orchestrator.configuration.NioFileSystemWatcherRunnable;
import org.jvi.tools.orchestrator.configuration.ThreadRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!dev")
@Configuration
public class DefaultConfiguration {

	@Value("${orchestrator.configurationPath:conf.d}")
	private String configurationPath;

	@Bean
	public ScheduledExecutorService scheduledExecutorService() {
		return Executors.newScheduledThreadPool(4);
	}

	@Bean
	@Autowired
	public ThreadRunner threadRunner(final FileSystemEventProcessor fileSystemEventProcessor) {
		final ThreadRunner threadRunner = new ThreadRunner(
				new NioFileSystemWatcherRunnable(Paths.get(configurationPath), fileSystemEventProcessor));
		threadRunner.start();
		return threadRunner;
	}

}
