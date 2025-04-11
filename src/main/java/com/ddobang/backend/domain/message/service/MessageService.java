package com.ddobang.backend.domain.message.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.message.dto.MessageDto;
import com.ddobang.backend.domain.message.entity.Message;
import com.ddobang.backend.domain.message.event.MessageCreatedEvent;
import com.ddobang.backend.domain.message.exception.MessageErrorCode;
import com.ddobang.backend.domain.message.exception.MessageException;
import com.ddobang.backend.domain.message.repository.MessageRepository;
import com.ddobang.backend.global.event.EventPublisher;
import com.ddobang.backend.global.response.SliceDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
	private final MessageRepository messageRepository;
	private final EventPublisher eventPublisher;

	// 쪽지 보내기
	public MessageDto sendMessage(Member sender, Member receiver, String content) {
		if (sender == null || receiver == null) {
			throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED);
		}
		try {
			Message message = messageRepository.save(Message.builder()
				.sender(sender)
				.receiver(receiver)
				.content(content)
				.isRead(false)
				.build());

			//이벤트: 메시지 생성 이벤트 발행
			eventPublisher.publish(MessageCreatedEvent.builder()
				.senderId(sender.getId())
				.senderNickname(sender.getNickname())
				.receiverId(receiver.getId())
				.receiverNickname(receiver.getNickname())
				.content(content)
				.messageId(message.getId())
				.build());

			return MessageDto.fromEntity(message);
		} catch (Exception e) {
			throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED);
		}
	}

	// 단일 메세지 조회 - DTO 반환
	@Transactional(readOnly = true)
	public MessageDto getMessage(Long id, Member member) {
		Message message = messageRepository.findById(id).orElseThrow(
			() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

		// 권한 체크 (보낸 사람 또는 받은 사람인지)
		if (!message.canAccess(member)) {
			throw new MessageException(MessageErrorCode.MESSAGE_ACCESS_FORBIDDEN);
		}

		return MessageDto.fromEntity(message);
	}

	// 커서 기반 무한 스크롤 - 받은 메시지 조회 - SliceDto 사용
	@Transactional(readOnly = true)
	public SliceDto<MessageDto> getReceivedMessagesWithCursor(Member member, LocalDateTime cursor, int size) {
		List<Message> messages;
		if (cursor == null) {
			// 첫 페이지 요청
			messages = messageRepository.findFirstReceivedMessages(member.getId(), size + 1);
		} else {
			// 다음 페이지 요청
			messages = messageRepository.findReceivedMessagesBeforeCursor(member.getId(), cursor, size + 1);
		}

		List<MessageDto> messageDtos = messages.stream()
			.map(MessageDto::fromEntity)
			.collect(Collectors.toList());

		return SliceDto.of(messageDtos, size);
	}

	// 커서 기반 무한 스크롤 - 보낸 메시지 조회 - SliceDto 사용
	@Transactional(readOnly = true)
	public SliceDto<MessageDto> getSentMessagesWithCursor(Member member, LocalDateTime cursor, int size) {
		List<Message> messages;
		if (cursor == null) {
			// 첫 페이지 요청
			messages = messageRepository.findFirstSentMessages(member.getId(), size + 1);
		} else {
			// 다음 페이지 요청
			messages = messageRepository.findSentMessagesBeforeCursor(member.getId(), cursor, size + 1);
		}

		List<MessageDto> messageDtos = messages.stream()
			.map(MessageDto::fromEntity)
			.collect(Collectors.toList());

		return SliceDto.of(messageDtos, size);
	}

	// 메시지 읽음 상태 변경
	public MessageDto updateIsRead(Long id, Member member) {
		Message message = messageRepository.findById(id)
			.orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

		// 수신자만 읽음 상태 변경 가능
		if (!message.canMarkAsRead(member)) {
			throw new MessageException(MessageErrorCode.MESSAGE_READ_FORBIDDEN);
		}

		// 이미 읽은 상태면 그대로 반환
		if (message.isRead()) {
			return MessageDto.fromEntity(message);
		}

		message.changeToRead();
		return MessageDto.fromEntity(messageRepository.save(message));
	}

	// 메시지 삭제
	public void deleteMessage(Long id, Member member) {
		Message message = messageRepository.findById(id)
			.orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

		// 수신자만 삭제 가능
		if (!message.canDelete(member)) {
			throw new MessageException(MessageErrorCode.MESSAGE_DELETE_FORBIDDEN);
		}

		messageRepository.delete(message);
	}

}