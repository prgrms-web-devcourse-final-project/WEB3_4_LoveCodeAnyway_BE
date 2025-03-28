package com.ddobang.backend.domain.message.dto;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.message.entity.Message;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageDto {

	private Long id;
	private Long senderId;
	private String senderNickname;
	private Long receiverId;
	private String receiverNickname;
	private String content;
	private boolean isRead;
	private LocalDateTime createdAt;

	public static MessageDto fromEntity(Message message) {
		return MessageDto.builder()
			.id(message.getId())
			.senderId(message.getSender().getId())
			.senderNickname(message.getSender().getNickname())
			.receiverId(message.getReceiver().getId())
			.receiverNickname(message.getReceiver().getNickname())
			.content(message.getContent())
			.isRead(message.isRead())
			.createdAt(message.getCreatedAt())
			.build();
	}
}
