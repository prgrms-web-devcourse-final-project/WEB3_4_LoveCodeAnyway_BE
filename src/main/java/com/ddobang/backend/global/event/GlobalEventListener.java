// package com.ddobang.backend.global.event;
//
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
//
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Propagation;
// import org.springframework.transaction.annotation.Transactional;
//
// import lombok.extern.slf4j.Slf4j;
//
// /**
//  * 전역 이벤트 리스너
//  * 모든 도메인 이벤트를 수신하고 적절한 리스너에게 전달하는 역할
//  */
// @Slf4j
// @Component
// public class GlobalEventListener {
//
// 	// 이벤트 타입 문자열 -> 리스너 맵핑
// 	private final Map<String, com.ddobang.backend.global.event.EventListener<DomainEvent>> eventListeners;
//
// 	// 생성자 주입으로 모든 이벤트 리스너 등록
// 	public GlobalEventListener(Map<String, com.ddobang.backend.global.event.EventListener> listeners) {
// 		this.eventListeners = new ConcurrentHashMap<>();
//
// 		// 모든 리스너를 순회하며 등록
// 		listeners.forEach((name, listener) -> {
// 			log.info("이벤트 리스너 발견: {}", name);
// 		});
// 	}
//
// 	/**
// 	 * 리스너 등록 - 애플리케이션 시작 후에도 동적으로 리스너 추가 가능
// 	 */
// 	public void registerListener(String eventType,
// 		com.ddobang.backend.global.event.EventListener<DomainEvent> listener) {
// 		eventListeners.put(eventType, listener);
// 		log.info("이벤트 리스너 등록: 이벤트 타입={}, 리스너={}",
// 			eventType, listener.getClass().getSimpleName());
// 	}
//
// 	/**
// 	 * 모든 도메인 이벤트를 수신하여 적절한 리스너에게 전달
// 	 */
// 	@EventListener
// 	@Transactional(propagation = Propagation.REQUIRES_NEW) // 이벤트 처리를 위한 별도 트랜잭션
// 	public void handleEvent(DomainEvent event) {
// 		final String eventType = event.getEventType();
// 		log.info("이벤트 수신: 타입={}, 클래스={}", eventType, event.getClass().getSimpleName());
//
// 		boolean handled = false;
//
// 		// 모든 리스너를 순회하며 이벤트 타입을 지원하는지 확인
// 		for (com.ddobang.backend.global.event.EventListener<DomainEvent> listener : eventListeners.values()) {
// 			if (listener.supportsEventType(eventType)) {
// 				try {
// 					log.debug("이벤트 전달: 타입={}, 리스너={}",
// 						eventType, listener.getClass().getSimpleName());
// 					listener.onEvent(event);
// 					handled = true;
// 				} catch (Exception e) {
// 					log.error("이벤트 처리 중 오류 발생: 타입={}, 리스너={}, 오류={}",
// 						eventType, listener.getClass().getSimpleName(), e.getMessage(), e);
// 				}
// 			}
// 		}
//
// 		if (!handled) {
// 			log.warn("이벤트에 대한 처리 리스너가 없음: 타입={}", eventType);
// 		}
// 	}
// }