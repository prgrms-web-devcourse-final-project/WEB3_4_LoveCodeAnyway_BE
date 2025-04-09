package com.ddobang.backend.domain.message.service.example;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.message.event.MessageSentEvent;
import com.ddobang.backend.global.event.EventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메시지 서비스 예시 - 이벤트 기반 알림 시스템 구현 예시
 * 실제 메시지 서비스에서는 이 코드를 참고하여 이벤트 발행 로직을 추가합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceExample {

    private final EventPublisher eventPublisher;
    
    /**
     * 메시지 전송 메소드 예시
     * 메시지 저장 후 이벤트를 발행합니다.
     */
    @Transactional
    public void sendMessage(Long messageId, Long senderId, String senderName, Long receiverId, String content) {
        // 1. 메시지 저장 로직 (실제 구현 시 추가)
        log.info("메시지 저장: senderId={}, receiverId={}, content={}", senderId, receiverId, content);
        
        // 2. 이벤트 발행
        MessageSentEvent event = new MessageSentEvent(messageId, senderId, senderName, receiverId);
        eventPublisher.publish(event);
        
        log.info("메시지 전송 완료 및 이벤트 발행: messageId={}", messageId);
    }
}
