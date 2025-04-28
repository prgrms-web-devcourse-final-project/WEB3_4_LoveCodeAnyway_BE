// package com.ddobang.backend.global.event;
//
// import org.springframework.stereotype.Component;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// /**
//  * 전역 이벤트 퍼블리셔
//  * 모든 도메인 이벤트를 발행하는 단일 진입점 역할
//  */
// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class GlobalEventPublisher {
//     private final EventPublisher eventPublisher;
//
//     /**
//      * 도메인 이벤트 발행
//      * @param event 발행할 도메인 이벤트
//      */
//     public void publishEvent(DomainEvent event) {
//         log.info("이벤트 발행: 타입={}, 클래스={}", event.getEventType(), event.getClass().getSimpleName());
//         eventPublisher.publish(event);
//     }
// }
