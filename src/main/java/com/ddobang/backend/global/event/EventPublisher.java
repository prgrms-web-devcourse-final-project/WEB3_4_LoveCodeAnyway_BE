package com.ddobang.backend.global.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 도메인 이벤트 발행 컴포넌트
 * Spring의 ApplicationEventPublisher를 활용하여 이벤트를 발행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {
	private final ApplicationEventPublisher publisher;

	/**
	 * 도메인 이벤트를 발행합니다.
	 * @param event 발행할 도메인 이벤트
	 */
	public void publish(DomainEvent event) {
		log.debug("도메인 이벤트 발행: type={}", event.getEventType());
		publisher.publishEvent(event);
	}
}
