package com.ddobang.backend.global.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 비동기 이벤트 처리를 위한 설정 클래스
 */
@Configuration
@EnableAsync
public class AsyncConfig {

	/**
	 * 알림 처리용 비동기 스레드 풀 설정
	 */
	@Bean(name = "alarmTaskExecutor")
	public Executor notificationTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(10);
		executor.setThreadNamePrefix("Notification-");
		executor.initialize();
		return executor;
	}
}
