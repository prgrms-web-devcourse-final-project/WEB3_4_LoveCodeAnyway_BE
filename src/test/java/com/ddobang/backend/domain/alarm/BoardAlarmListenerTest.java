package com.ddobang.backend.domain.alarm;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.board.event.PostReplyCreatedEvent;
import com.ddobang.backend.domain.board.listener.BoardAlarmListener;

@ExtendWith(MockitoExtension.class)
public class BoardAlarmListenerTest {

	@Mock
	private AlarmService alarmService;

	@Mock
	private AlarmEventService alarmEventService;

	@InjectMocks
	private BoardAlarmListener listener;

	private PostReplyCreatedEvent event;
	private AlarmResponse mockResponse;

	@BeforeEach
	void setUp() {
		// 테스트 이벤트 설정
		event = PostReplyCreatedEvent.builder()
			.postId(100L)
			.postTitle("배송 관련 문의드립니다")
			.postOwnerId(50L)
			.replyContent("안녕하세요, 고객님. 문의하신 내용에 대해 답변드립니다...")
			.build();

		// 모의 알림 응답 설정
		mockResponse = AlarmResponse.builder()
			.id(1L)
			.receiverId(50L)
			.title("문의하신 글에 답변이 등록되었습니다")
			.content("'배송 관련 문의드립니다' 문의에 답변이 등록되었습니다: 안녕하세요, 고객님...")
			.alarmType(AlarmType.SYSTEM)
			.readStatus(false)
			.relId(100L)
			.build();
	}

	@Test
	@DisplayName("문의 답변 이벤트 발생 시 문의 작성자에게 알림이 전송되어야 한다")
	void handlePostReplyCreatedEventTest() {
		// given
		when(alarmService.createAlarm(any())).thenReturn(mockResponse);

		// when
		listener.handlePostReplyCreatedEvent(event);

		// then
		ArgumentCaptor<com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest> captor =
			ArgumentCaptor.forClass(com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest.class);

		verify(alarmService, times(1)).createAlarm(captor.capture());

		com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest request = captor.getValue();
		assertThat(request.getReceiverId()).isEqualTo(50L);
		assertThat(request.getTitle()).contains("답변이 등록");
		assertThat(request.getContent()).contains("배송 관련 문의드립니다");
		assertThat(request.getAlarmType()).isEqualTo(AlarmType.POST_REPLY);
		assertThat(request.getRelId()).isEqualTo(100L);

		verify(alarmEventService, times(1)).sendNotification(eq(50L), any());
	}

	@Test
	@DisplayName("알림 서비스 예외 발생시에도 리스너는 예외를 전파하지 않아야 한다")
	void handlePostReplyCreatedEventExceptionTest() {
		// given
		when(alarmService.createAlarm(any())).thenThrow(new RuntimeException("알림 서비스 오류"));

		// when & then
		// 예외가 발생하지 않고 정상적으로 실행되어야 함
		listener.handlePostReplyCreatedEvent(event);

		// 예외가 발생했으므로 sendNotification은 호출되지 않아야 함
		verify(alarmEventService, never()).sendNotification(any(), any());
	}
}