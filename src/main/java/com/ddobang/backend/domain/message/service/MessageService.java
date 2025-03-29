package com.ddobang.backend.domain.message.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.entity.Member;
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

	public Message sendMessage(Member sender, Member receiver, String content) {
		try {
			if (sender == null || receiver == null) {
				throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED);
			}

			return messageRepository.save(Message.builder()
				.sender(sender)
				.receiver(receiver)
				.content(content)
				.isRead(false)
				.build());
		} catch (Exception e) {
			throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED);
		}
	}

	// 단건 조회
	@Transactional(readOnly = true)
	public Message getMessage(Long id) {
		return messageRepository.findById(id).orElseThrow(
			() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));
	}

	// 다건 조회
	@Transactional(readOnly = true)
	public List<Message> getAllMessages(Long receiverId) {
		return messageRepository.findAllByReceiverId(receiverId);
	}

	// 쪽지 읽음상태로 변경
	public Message updateIsRead(Long id, boolean isRead) {
		Message message = getMessage(id);
		message.changeToRead();
		return messageRepository.save(message);
	}

	// 쪽지 삭제도 softdelete?
	// 일단은 delete해버림
	public void deleteMessage(Long id) {
		Message message = getMessage(id);
		messageRepository.delete(message);
	}

}