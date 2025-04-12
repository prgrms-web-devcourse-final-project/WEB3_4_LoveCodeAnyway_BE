package com.ddobang.backend.domain.alarm;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.repository.AlarmRepository;
import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.domain.board.event.PostReplyCreatedEvent;
import com.ddobang.backend.domain.board.listener.BoardAlarmListener;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.support.MemberTestFactory;

@ExtendWith(MockitoExtension.class)
public class BoardAlarmListenerTest {

	@Mock
	private AlarmEventService alarmEventService;

	@Mock
	private MemberService memberService;

	@Mock
	private AlarmRepository alarmRepository;

	@InjectMocks
	private BoardAlarmListener listener;

	private PostReplyCreatedEvent event;
	private Member postOwner;

	@BeforeEach
	void setUp() {
		// 테스트 이벤트 설정
		event = PostReplyCreatedEvent.builder()
			.postId(100L)
			.postTitle("배송 관련 문의드립니다")
			.postOwnerId(50L)
			.replyContent("안녕하세요, 고객님. 문의하신 내용에 대해 답변드립니다...")
			.build();
	}

	@Test
	@DisplayName("문의 답변 이벤트 발생 시 문의 작성자에게 알림이 전송되어야 한다")
	void handlePostReplyCreatedEventTest() {
		// Given
		postOwner = MemberTestFactory.withNickname("게시글작성자");
		when(memberService.getMemberById(50L)).thenReturn(postOwner);

		// 저장된 Alarm 모의 객체 설정 - 필요한 부분만 설정
		Alarm savedAlarm = mock(Alarm.class);
		when(savedAlarm.getId()).thenReturn(1L);
		when(savedAlarm.getReceiver()).thenReturn(postOwner);
		when(alarmRepository.save(any(Alarm.class))).thenReturn(savedAlarm);

		// When
		listener.handlePostReplyCreatedEvent(event);

		// Then
		// Alarm 저장 확인
		verify(alarmRepository, times(1)).save(any(Alarm.class));

		// Notification 전송 확인
		verify(alarmEventService, times(1)).sendNotification(eq(50L), any(AlarmResponse.class));
	}

	@Test
	@DisplayName("알림 서비스 예외 발생시에도 리스너는 예외를 전파하지 않아야 한다")
	void handlePostReplyCreatedEventExceptionTest() {
		// Given - 각 테스트에서 별도로 stubbing 설정
		when(memberService.getMemberById(any())).thenThrow(new RuntimeException("멤버 서비스 오류"));

		// When & Then - 예외가 전파되지 않고 정상적으로 실행되어야 함
		listener.handlePostReplyCreatedEvent(event);

		// 예외가 발생했으므로 저장과 알림 발송이 호출되지 않아야 함
		verify(alarmRepository, never()).save(any(Alarm.class));
		verify(alarmEventService, never()).sendNotification(any(), any());
	}
}