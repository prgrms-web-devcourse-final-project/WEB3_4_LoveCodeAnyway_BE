package com.ddobang.backend.domain.message.service;

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

import lombok.RequiredArgsConstructor;

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

	// // 무한 스크롤 - 받은 메시지 목록 조회
	// @Transactional(readOnly = true)
	// public SliceDto<MessageDto> getReceivedMessagesInfinite(Member member, Long cursorId, int size) {
	// 	Pageable pageable = PageRequest.of(0, size + 1);
	// 	List<Message> messages;
	//
	// 	if (cursorId == null) {
	// 		// 첫 페이지 요청
	// 		messages = messageRepository.findReceivedMessagesFirstPage(member.getId(), pageable);
	// 	} else {
	// 		// 다음 페이지 요청
	// 		messages = messageRepository.findReceivedMessagesNextPage(member.getId(), cursorId, pageable);
	// 	}
	//
	// 	// MessageDto로 변환
	// 	List<MessageDto> messageDtos = messages.stream()
	// 		.map(MessageDto::fromEntity)
	// 		.collect(Collectors.toList());
	//
	// 	// SliceDto로 변환 (자동으로 size+1개를 확인하여 hasNext 계산)
	// 	return SliceDto.of(messageDtos, size);
	// }
	//
	// // 무한 스크롤 - 보낸 메시지 목록 조회
	// @Transactional(readOnly = true)
	// public SliceDto<MessageDto> getSentMessagesInfinite(Member member, Long cursorId, int size) {
	// 	Pageable pageable = PageRequest.of(0, size + 1);
	// 	List<Message> messages;
	//
	// 	if (cursorId == null) {
	// 		// 첫 페이지 요청
	// 		messages = messageRepository.findSentMessagesFirstPage(member.getId(), pageable);
	// 	} else {
	// 		// 다음 페이지 요청
	// 		messages = messageRepository.findSentMessagesNextPage(member.getId(), cursorId, pageable);
	// 	}
	//
	// 	// MessageDto로 변환
	// 	List<MessageDto> messageDtos = messages.stream()
	// 		.map(MessageDto::fromEntity)
	// 		.collect(Collectors.toList());
	//
	// 	// SliceDto로 변환
	// 	return SliceDto.of(messageDtos, size);
	// }

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