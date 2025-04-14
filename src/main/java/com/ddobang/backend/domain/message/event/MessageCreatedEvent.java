// 메시지 생성 이벤트 클래스
package com.ddobang.backend.domain.message.event;

import com.ddobang.backend.global.event.DomainEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageCreatedEvent implements DomainEvent {
	private Long senderId;
	private String senderNickname;
	private Long receiverId;
	private String receiverNickname;
	private String content;
	private Long messageId;

	@Override
	public String getEventType() {
		return "MESSAGE_CREATED_EVENT";
	}
}