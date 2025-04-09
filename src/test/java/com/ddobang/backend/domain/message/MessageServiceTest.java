package com.ddobang.backend.domain.message;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.message.dto.MessageDto;
import com.ddobang.backend.domain.message.entity.Message;
import com.ddobang.backend.domain.message.exception.MessageErrorCode;
import com.ddobang.backend.domain.message.exception.MessageException;
import com.ddobang.backend.domain.message.repository.MessageRepository;
import com.ddobang.backend.domain.message.service.MessageService;
import com.ddobang.backend.global.response.SliceDto;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private MessageRepository messageRepository;

	@InjectMocks
	private MessageService messageService;

	private Member sender;
	private Member receiver;
	private Member otherMember;
	private Message message;
	private List<Message> messages;

	@BeforeEach
	void setUp() throws Exception {
		// 테스트용 데이터 설정
		sender = Member.builder()
			.nickname("sender")
			.gender(Gender.MALE)
			.introduction("테스트 송신자")
			.build();

		receiver = Member.builder()
			.nickname("receiver")
			.gender(Gender.FEMALE)
			.introduction("테스트 수신자")
			.build();

		otherMember = Member.builder()
			.nickname("other")
			.gender(Gender.MALE)
			.introduction("제3자")
			.build();

		// Member ID 설정 (리플렉션 사용)
		setMemberId(sender, 1L);
		setMemberId(receiver, 2L);
		setMemberId(otherMember, 3L);

		// 단일 메시지 생성
		message = Message.builder()
			.sender(sender)
			.receiver(receiver)
			.content("테스트 메시지")
			.isRead(false)
			.build();

		// 메시지 ID 설정 (리플렉션 사용)
		setMessageId(message, 1L);

		// 메시지 리스트 생성
		messages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Message msg = Message.builder()
				.sender(sender)
				.receiver(receiver)
				.content("테스트 메시지 " + i)
				.isRead(false)
				.build();

			// 메시지 ID 설정 (리플렉션 사용)
			setMessageId(msg, (long)(i + 1));

			messages.add(msg);
		}
	}

	// 리플렉션을 사용하여 Member ID 설정
	private void setMemberId(Member member, Long id) throws Exception {
		java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(member, id);
	}

	// 리플렉션을 사용하여 Message ID 설정
	private void setMessageId(Message message, Long id) throws Exception {
		java.lang.reflect.Field idField = Message.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(message, id);
	}

	@Test
	@DisplayName("메시지 전송 성공 테스트")
	void sendMessage_Success() {
		// given
		when(messageRepository.save(any(Message.class))).thenReturn(message);

		// when
		MessageDto result = messageService.sendMessage(sender, receiver, "테스트 메시지");

		// then
		assertThat(result).isNotNull();
		assertEquals(sender.getId(), result.getSenderId());
		assertEquals(receiver.getId(), result.getReceiverId());
		assertEquals("테스트 메시지", result.getContent());
		verify(messageRepository, times(1)).save(any(Message.class));
	}

	@Test
	@DisplayName("메시지 전송 실패 - 송신자 null")
	void sendMessage_Failure_SenderNull() {
		// when & then
		assertThrows(MessageException.class, () -> {
			messageService.sendMessage(null, receiver, "테스트 메시지");
		});
	}

	@Test
	@DisplayName("메시지 조회 성공")
	void getMessage_Success() {
		// given
		when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));

		// when
		MessageDto result = messageService.getMessage(1L, receiver);

		// then
		assertThat(result).isNotNull();
		assertEquals(1L, result.getId());
		assertEquals("테스트 메시지", result.getContent());
	}

	@Test
	@DisplayName("메시지 조회 실패 - 권한 없음")
	void getMessage_Failure_NoPermission() {
		// given
		when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));

		// when & then
		assertThrows(MessageException.class, () -> {
			messageService.getMessage(1L, otherMember);
		}, MessageErrorCode.MESSAGE_ACCESS_FORBIDDEN.getMessage());
	}

	@Test
	@DisplayName("커서 기반 받은 메시지 조회")
	void getReceivedMessagesWithCursor() {
		// given
		when(messageRepository.findByReceiverIdAndIdLessThanOrderByIdDesc(eq(receiver.getId()), eq(null),
			any(Pageable.class)))
			.thenReturn(messages);

		// when
		SliceDto<MessageDto> result = messageService.getReceivedMessagesWithCursor(receiver, null, 10);

		// then
		assertThat(result).isNotNull();
		assertThat(result.content()).hasSize(10);
		verify(messageRepository, times(1)).findByReceiverIdAndIdLessThanOrderByIdDesc(eq(receiver.getId()), eq(null),
			any(Pageable.class));
	}

	@Test
	@DisplayName("메시지 읽음 상태 변경")
	void updateIsRead() throws Exception {
		// given
		when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));

		Message readMessage = Message.builder()
			.sender(sender)
			.receiver(receiver)
			.content("테스트 메시지")
			.isRead(true)
			.build();

		// 메시지 ID 설정 (리플렉션 사용)
		setMessageId(readMessage, 1L);

		when(messageRepository.save(any(Message.class))).thenReturn(readMessage);

		// when
		MessageDto result = messageService.updateIsRead(1L, receiver);

		// then
		assertThat(result).isNotNull();
		assertThat(result.isRead()).isTrue();
	}

	@Test
	@DisplayName("메시지 삭제")
	void deleteMessage() {
		// given
		when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));

		// when
		messageService.deleteMessage(1L, receiver);

		// then
		verify(messageRepository, times(1)).delete(any(Message.class));
	}
}
