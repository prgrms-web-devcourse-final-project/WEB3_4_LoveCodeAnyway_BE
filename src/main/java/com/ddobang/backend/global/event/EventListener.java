package com.ddobang.backend.global.event;

// 이벤트 구독자 인터페이스
public interface EventListener<T extends DomainEvent> {
	void onEvent(T event);

	boolean supportsEventType(String eventType);
}
