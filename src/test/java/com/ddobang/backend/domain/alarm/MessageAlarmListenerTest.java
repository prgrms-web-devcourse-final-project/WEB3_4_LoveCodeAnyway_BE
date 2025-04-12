package com.ddobang.backend.domain.alarm;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.listener.MessageAlarmListener;
import com.ddobang.backend.domain.alarm.repository.AlarmRepository;
import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.message.event.MessageCreatedEvent;
import com.ddobang.backend.support.MemberTestFactory;

@ExtendWith(MockitoExtension.class)
public class MessageAlarmListenerTest {

	@Mock
	private AlarmService alarmService;

	@Mock
	private AlarmEventService alarmEventService;
	
	@Mock
	private MemberService memberService;
	
	@Mock
	private AlarmRepository alarmRepository;

	@InjectMocks
	private MessageAlarmListener messageAlarmListener;

	@Test
	@DisplayName("메시지 생성 이벤트 발생 시 알림이 생성되어야 한다")
	void handleMessageCreatedEvent_ShouldCreateAlarm() {
		// Given
		Long senderId = 1L;
		String senderNickname = "김보내";
		Long receiverId = 2L;
		String receiverNickname = "이받아";
		String content = "안녕하세요, 테스트 메시지입니다.";
		Long messageId = 10L;

		MessageCreatedEvent event = MessageCreatedEvent.builder()
			.senderId(senderId)
			.senderNickname(senderNickname)
			.receiverId(receiverId)
			.receiverNickname(receiverNickname)
			.content(content)
			.messageId(messageId)
			.build();

		// Member 객체 생성
		Member receiver = MemberTestFactory.withNickname(receiverNickname);
		when(memberService.getMemberById(receiverId)).thenReturn(receiver);
		
		// 저장된 Alarm 모의 객체 생성
		Alarm savedAlarm = mock(Alarm.class);
		when(savedAlarm.getId()).thenReturn(1L);
		when(savedAlarm.getReceiver()).thenReturn(receiver);
		when(savedAlarm.getTitle()).thenReturn("새 쪽지가 도착했습니다.");
		when(savedAlarm.getContent()).thenReturn(senderNickname + "님으로부터 쪽지가 도착했습니다.");
		when(savedAlarm.getAlarmType()).thenReturn(AlarmType.MESSAGE);
		when(savedAlarm.getReadStatus()).thenReturn(false);
		when(savedAlarm.getRelId()).thenReturn(messageId);
		
		when(alarmRepository.save(any(Alarm.class))).thenReturn(savedAlarm);

		// When
		messageAlarmListener.handleMessageCreatedEvent(event);

		// Then
		// Alarm 저장 확인
		verify(alarmRepository, times(1)).save(any(Alarm.class));
		
		// AlarmEventService.sendNotification() 호출 확인
		verify(alarmEventService, times(1)).sendNotification(eq(receiverId), any(AlarmResponse.class));
	}

	@Test
	@DisplayName("알림 서비스 예외 발생시 메시지 기능에 영향이 없어야 한다")
	void handleMessageCreatedEvent_WhenAlarmServiceThrowsException_ShouldNotPropagateException() {
		// Given
		MessageCreatedEvent event = MessageCreatedEvent.builder()
			.senderId(1L)
			.senderNickname("김보내")
			.receiverId(2L)
			.receiverNickname("이받아")
			.content("안녕하세요")
			.messageId(10L)
			.build();

		// MemberService가 예외를 던지도록 설정
		when(memberService.getMemberById(any())).thenThrow(new RuntimeException("멤버 서비스 오류"));

		// When & Then
		// 예외가 전파되지 않고 핸들러 내부에서 처리되어야 함
		messageAlarmListener.handleMessageCreatedEvent(event);

		// 예외가 발생해도 sendNotification은 호출되지 않아야 함
		verify(alarmEventService, never()).sendNotification(any(), any());
	}
}