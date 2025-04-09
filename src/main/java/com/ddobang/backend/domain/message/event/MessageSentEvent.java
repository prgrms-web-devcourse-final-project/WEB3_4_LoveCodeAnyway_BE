package com.ddobang.backend.domain.message.event;

import com.ddobang.backend.global.event.DomainEvent;

import lombok.Getter;

/**
 * 메시지 전송 이벤트
 * 메시지가 전송되었을 때 발생하는 이벤트입니다.
 */
@Getter
public class MessageSentEvent implements DomainEvent {
    private final Long messageId;
    private final Long senderId;
    private final String senderName;
    private final Long receiverId;
    
    public MessageSentEvent(Long messageId, Long senderId, String senderName, Long receiverId) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
    }
    
    @Override
    public String getEventType() {
        return "MESSAGE_SENT";
    }
}
