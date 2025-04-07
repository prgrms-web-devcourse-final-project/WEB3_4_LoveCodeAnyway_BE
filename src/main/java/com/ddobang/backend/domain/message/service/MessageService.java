package com.ddobang.backend.domain.message.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.message.dto.MessageDto;
import com.ddobang.backend.domain.message.entity.Message;
import com.ddobang.backend.domain.message.exception.MessageErrorCode;
import com.ddobang.backend.domain.message.exception.MessageException;
import com.ddobang.backend.domain.message.repository.MessageRepository;
import com.ddobang.backend.global.response.SliceDto;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
	private final MessageRepository messageRepository;
	private final AlarmService alarmService;
	private final AlarmEventService alarmEventService;

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

		// 권한 체크
		if (!message.hasAccessPermission(member)) {
			throw new MessageException(MessageErrorCode.MESSAGE_ACCESS_FORBIDDEN);
		}

		return MessageDto.fromEntity(message);
	}

	// 커서 기반 무한 스크롤을 위한 받은 메시지 목록 조회
	@Transactional(readOnly = true)
	public SliceDto<MessageDto> getReceivedMessagesWithCursor(Member member, Long lastMessageId, int size) {
		Pageable pageable = PageRequest.of(0, size + 1); // +1로 다음 페이지 존재여부 확인
		
		List<Message> messages = messageRepository.findByReceiverIdAndIdLessThanOrderByIdDesc(
			member.getId(), lastMessageId, pageable);
		
		// MessageDto로 변환
		var messageDtos = messages.stream()
			.map(MessageDto::fromEntity)
			.toList();
		
		return SliceDto.of(messageDtos, size);
	}
	
	// 커서 기반 무한 스크롤을 위한 보낸 메시지 목록 조회
	@Transactional(readOnly = true)
	public SliceDto<MessageDto> getSentMessagesWithCursor(Member member, Long lastMessageId, int size) {
		Pageable pageable = PageRequest.of(0, size + 1); // +1로 다음 페이지 존재여부 확인
		
		List<Message> messages = messageRepository.findBySenderIdAndIdLessThanOrderByIdDesc(
			member.getId(), lastMessageId, pageable);
		
		// MessageDto로 변환
		var messageDtos = messages.stream()
			.map(MessageDto::fromEntity)
			.toList();
		
		return SliceDto.of(messageDtos, size);
	}

	// 메시지 읽음 상태 변경
	public MessageDto updateIsRead(Long id, Member member) {
		Message message = messageRepository.findById(id)
			.orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

		// 수신자만 읽음 상태 변경 가능
		if (!message.hasReadPermission(member)) {
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
		if (!message.hasDeletePermission(member)) {
			throw new MessageException(MessageErrorCode.MESSAGE_DELETE_FORBIDDEN);
		}

		messageRepository.delete(message);
	}
}