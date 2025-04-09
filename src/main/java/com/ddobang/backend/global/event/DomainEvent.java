package com.ddobang.backend.global.event;

/**
 * 도메인 이벤트 인터페이스
 * 모든 도메인 이벤트는 이 인터페이스를 구현해야 합니다.
 */
public interface DomainEvent {
	/**
	 * 이벤트 타입을 반환합니다.
	 * @return 이벤트 타입 문자열
	 */
	String getEventType();
}
