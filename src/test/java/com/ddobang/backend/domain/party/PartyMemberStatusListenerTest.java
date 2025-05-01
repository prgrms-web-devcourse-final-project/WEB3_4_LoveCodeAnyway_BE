package com.ddobang.backend.domain.party;

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
import com.ddobang.backend.domain.party.event.PartyApplyEvent;
import com.ddobang.backend.domain.party.event.PartyMemberStatusUpdatedEvent;
import com.ddobang.backend.domain.party.listener.PartyMemberStatusListener;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;

@ExtendWith(MockitoExtension.class)
public class PartyMemberStatusListenerTest {

	@Mock
	private AlarmService alarmService;

	@Mock
	private AlarmEventService alarmEventService;

	@InjectMocks
	private PartyMemberStatusListener listener;

	private PartyApplyEvent applyEvent;
	private PartyMemberStatusUpdatedEvent acceptEvent;
	private PartyMemberStatusUpdatedEvent rejectEvent;
	private AlarmResponse mockAlarmResponse;

	@BeforeEach
	void setUp() {
		// 모임 신청 이벤트 준비
		applyEvent = PartyApplyEvent.builder()
			.partyId(1L)
			.partyTitle("방탈출 모임")
			.hostId(10L)
			.applicantId(20L)
			.applicantNickname("신청자")
			.build();

		// 모임 신청 승인 이벤트 준비
		acceptEvent = PartyMemberStatusUpdatedEvent.builder()
			.partyId(1L)
			.partyTitle("방탈출 모임")
			.memberId(20L)
			.hostId(10L)
			.hostNickname("모임장")
			.newStatus(PartyMemberStatus.ACCEPTED)
			.build();

		// 모임 신청 거절 이벤트 준비
		rejectEvent = PartyMemberStatusUpdatedEvent.builder()
			.partyId(1L)
			.partyTitle("방탈출 모임")
			.memberId(20L)
			.hostId(10L)
			.hostNickname("모임장")
			.newStatus(PartyMemberStatus.CANCELLED)
			.build();

		// Mock 알람 응답 준비
		mockAlarmResponse = AlarmResponse.builder()
			.id(1L)
			.receiverId(10L)
			.title("테스트 알림")
			.content("테스트 내용")
			.alarmType(AlarmType.SUBSCRIBE)
			.readStatus(false)
			.relId(1L)
			.build();
	}

	@Test
	@DisplayName("모임 신청 이벤트 발생 시 모임장에게 알림이 전송되어야 한다")
	void handlePartyApplyEventTest() {
		// given
		when(alarmService.createAlarm(any())).thenReturn(mockAlarmResponse);

		// when
		listener.handlePartyApplyEvent(applyEvent);

		// then
		ArgumentCaptor<com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest> captor =
			ArgumentCaptor.forClass(com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest.class);

		verify(alarmService, times(1)).createAlarm(captor.capture());

		com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest request = captor.getValue();
		assertThat(request.getReceiverId()).isEqualTo(10L);
		assertThat(request.getTitle()).isEqualTo("새로운 모임 참가 신청이 있습니다");
		assertThat(request.getContent()).contains("신청자");
		assertThat(request.getContent()).contains("방탈출 모임");
		assertThat(request.getAlarmType()).isEqualTo(AlarmType.SUBSCRIBE);
		assertThat(request.getRelId()).isEqualTo(1L);

		verify(alarmEventService, times(1)).sendNotification(eq(10L), any());
	}

	@Test
	@DisplayName("모임 신청 승인 시 신청자에게 알림이 전송되어야 한다")
	void handlePartyMemberStatusUpdatedEventAcceptTest() {
		// given
		when(alarmService.createAlarm(any())).thenReturn(mockAlarmResponse);

		// when
		listener.handlePartyMemberStatusUpdatedEvent(acceptEvent);

		// then
		ArgumentCaptor<com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest> captor =
			ArgumentCaptor.forClass(com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest.class);

		verify(alarmService, times(1)).createAlarm(captor.capture());

		com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest request = captor.getValue();
		assertThat(request.getReceiverId()).isEqualTo(20L);
		assertThat(request.getTitle()).contains("승인");
		assertThat(request.getContent()).contains("모임장");
		assertThat(request.getContent()).contains("방탈출 모임");
		assertThat(request.getAlarmType()).isEqualTo(AlarmType.SUBSCRIBE);
		assertThat(request.getRelId()).isEqualTo(1L);

		verify(alarmEventService, times(1)).sendNotification(eq(20L), any());
	}

	@Test
	@DisplayName("모임 신청 거절 시 신청자에게 알림이 전송되어야 한다")
	void handlePartyMemberStatusUpdatedEventRejectTest() {
		// given
		when(alarmService.createAlarm(any())).thenReturn(mockAlarmResponse);

		// when
		listener.handlePartyMemberStatusUpdatedEvent(rejectEvent);

		// then
		ArgumentCaptor<com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest> captor =
			ArgumentCaptor.forClass(com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest.class);

		verify(alarmService, times(1)).createAlarm(captor.capture());

		com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest request = captor.getValue();
		assertThat(request.getReceiverId()).isEqualTo(20L);
		assertThat(request.getTitle()).contains("거절");
		assertThat(request.getContent()).contains("모임장");
		assertThat(request.getContent()).contains("방탈출 모임");
		assertThat(request.getAlarmType()).isEqualTo(AlarmType.SUBSCRIBE);
		assertThat(request.getRelId()).isEqualTo(1L);

		verify(alarmEventService, times(1)).sendNotification(eq(20L), any());
	}

	@Test
	@DisplayName("알림 서비스 예외 발생시에도 리스너는 예외를 전파하지 않아야 한다")
	void handleExceptionTest() {
		// given
		when(alarmService.createAlarm(any())).thenThrow(new RuntimeException("알림 서비스 오류"));

		// when & then
		// 예외가 발생하지 않고 정상적으로 실행되어야 함
		listener.handlePartyApplyEvent(applyEvent);

		// 예외가 발생했으므로 sendNotification은 호출되지 않아야 함
		verify(alarmEventService, never()).sendNotification(any(), any());
	}
}
