package com.ddobang.backend.global.event;

// 이벤트 발행자 인터페이스
public interface EventPublisher {
	void publish(DomainEvent event);
}
