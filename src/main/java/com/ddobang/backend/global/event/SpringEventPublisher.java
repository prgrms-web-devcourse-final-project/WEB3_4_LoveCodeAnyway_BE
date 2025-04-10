package com.ddobang.backend.global.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringEventPublisher implements EventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void publish(DomainEvent event) {
		applicationEventPublisher.publishEvent(event);
	}
}