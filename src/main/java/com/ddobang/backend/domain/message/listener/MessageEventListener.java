package com.ddobang.backend.domain.message.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.event.AlarmEvent;
import com.ddobang.backend.domain.message.event.MessageCreatedEvent;
import com.ddobang.backend.global.event.DomainEvent;
import com.ddobang.backend.global.event.EventListener;
import com.ddobang.backend.global.event.GlobalEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메시지 이벤트 리스너
 * 모든 메시지 관련 이벤트를 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventListener implements EventListener<DomainEvent> {

    private final GlobalEventPublisher eventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 별도 트랜잭션으로 처리
    public void onEvent(DomainEvent event) {
        if (event instanceof MessageCreatedEvent) {
            handleMessageCreatedEvent((MessageCreatedEvent) event);
        } else {
            log.warn("지원하지 않는 이벤트 타입: {}", event.getClass().getName());
        }
    }

    @Override
    public boolean supportsEventType(String eventType) {
        return "MESSAGE_CREATED_EVENT".equals(eventType);
    }

    private void handleMessageCreatedEvent(MessageCreatedEvent event) {
        log.info("메시지 생성 이벤트 수신: 발신자={}, 수신자={}",
            event.getSenderNickname(), event.getReceiverNickname());

        // 메시지 생성 이벤트를 받으면 알람 이벤트를 발행
        AlarmEvent alarmEvent = AlarmEvent.builder()
            .receiverId(event.getReceiverId())
            .title("새 쪽지가 도착했습니다.")
            .content(event.getSenderNickname() + "님으로부터 쪽지가 도착했습니다.")
            .alarmType(AlarmType.MESSAGE.name())
            .relId(event.getMessageId())
            .build();

        try {
            // 알람 이벤트 발행
            eventPublisher.publishEvent(alarmEvent);
            log.info("메시지 알람 이벤트 발행 완료: 수신자={}, 메시지 ID={}", 
                event.getReceiverId(), event.getMessageId());
        } catch (Exception e) {
            log.error("메시지 알람 이벤트 발행 중 오류 발생", e);
        }
    }
}
