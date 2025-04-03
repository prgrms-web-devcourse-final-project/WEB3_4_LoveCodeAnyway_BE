package com.ddobang.backend.domain.alarm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.exception.AlarmErrorCode;
import com.ddobang.backend.domain.alarm.exception.SseException;
import com.ddobang.backend.domain.alarm.infra.EmitterRepository;

@ExtendWith(MockitoExtension.class)
class AlarmEventServiceTest {

	@Mock
	private EmitterRepository emitterRepository;

	@InjectMocks
	private AlarmEventService alarmEventService;

	private Long userId;
	private AlarmResponse alarmResponse;

	@BeforeEach
	void setUp() {
		userId = 1L;

		alarmResponse = AlarmResponse.builder()
			.id(1L)
			.title("테스트 알림")
			.content("테스트 내용입니다.")
			.alarmType(AlarmType.SYSTEM)
			.build();
	}

	@Test
	@DisplayName("SSE 구독 성공 테스트")
	void subscribeSuccessTest() {
		// Given
		// 실제 구현에서는 새로운 SseEmitter를 생성하고 이를 저장 후 반환함
		doNothing().when(emitterRepository).remove(userId);
		when(emitterRepository.save(eq(userId), any(SseEmitter.class))).thenAnswer(invocation -> {
			return invocation.getArgument(1); // 두 번째 인자 (SseEmitter)를 그대로 반환
		});

		// When
		SseEmitter result = alarmEventService.subscribe(userId);

		// Then
		assertNotNull(result);
		verify(emitterRepository, times(1)).remove(userId); // 기존 연결 제거 확인
		verify(emitterRepository, times(1)).save(eq(userId), any(SseEmitter.class)); // 새 연결 저장 확인
	}

	@Test
	@DisplayName("SSE 알림 예외 처리 테스트")
	void sseNotificationExceptionHandlingTest() {
		// 이 테스트에서는 IOException을 직접 발생시키기 어려우므로 대안적 방법으로 테스트

		// Given
		// 1. save 메서드는 성공적으로 호출되도록 설정
		doNothing().when(emitterRepository).remove(userId);
		when(emitterRepository.save(eq(userId), any(SseEmitter.class))).thenReturn(mock(SseEmitter.class));

		// 2. SSE 연결 후 예외 시나리오 기대하기
		// 알림 전송 시 예외 발생
		doThrow(new SseException(AlarmErrorCode.SSE_CONNECTION_ERROR))
			.when(emitterRepository).sendToUser(eq(userId), any(), any(), any());

		// When & Then
		// 구독은 성공적으로 수행됨
		SseEmitter emitter = alarmEventService.subscribe(userId);
		assertNotNull(emitter);

		// 알림 전송 시 예외 처리 확인
		assertDoesNotThrow(() -> {
			alarmEventService.sendNotification(userId, alarmResponse);
		});

		// 검증
		verify(emitterRepository).remove(userId);
		verify(emitterRepository).save(eq(userId), any(SseEmitter.class));
		verify(emitterRepository).sendToUser(eq(userId), any(), any(), any());
	}

	@Test
	@DisplayName("알림 전송 성공 테스트")
	void sendNotificationSuccessTest() {
		// Given
		doNothing().when(emitterRepository).sendToUser(
			eq(userId),
			eq(alarmResponse),
			eq("alarm"),
			eq(alarmResponse.getId().toString())
		);

		// When & Then - 예외가 발생하지 않아야 함
		assertDoesNotThrow(() -> {
			alarmEventService.sendNotification(userId, alarmResponse);
		});

		verify(emitterRepository, times(1)).sendToUser(
			eq(userId),
			eq(alarmResponse),
			eq("alarm"),
			eq(alarmResponse.getId().toString())
		);
	}

	@Test
	@DisplayName("알림 전송 실패 시 예외 처리 테스트")
	void sendNotificationExceptionHandlingTest() {
		// Given
		doThrow(new SseException(AlarmErrorCode.SSE_SEND_ERROR))
			.when(emitterRepository).sendToUser(
				eq(userId),
				eq(alarmResponse),
				eq("alarm"),
				eq(alarmResponse.getId().toString())
			);

		// When & Then - 예외가 전파되지 않아야 함 (내부에서 처리됨)
		assertDoesNotThrow(() -> {
			alarmEventService.sendNotification(userId, alarmResponse);
		});

		verify(emitterRepository, times(1)).sendToUser(
			eq(userId),
			eq(alarmResponse),
			eq("alarm"),
			eq(alarmResponse.getId().toString())
		);
	}

	@Test
	@DisplayName("다중 사용자 알림 전송 테스트")
	void sendNotificationToMultipleUsersTest() {
		// Given
		List<Long> userIds = Arrays.asList(1L, 2L, 3L);

		for (Long uid : userIds) {
			doNothing().when(emitterRepository).sendToUser(
				eq(uid),
				eq(alarmResponse),
				eq("alarm"),
				eq(alarmResponse.getId().toString())
			);
		}

		// When
		userIds.forEach(uid -> alarmEventService.sendNotification(uid, alarmResponse));

		// Then
		for (Long uid : userIds) {
			verify(emitterRepository, times(1)).sendToUser(
				eq(uid),
				eq(alarmResponse),
				eq("alarm"),
				eq(alarmResponse.getId().toString())
			);
		}
	}

	@Test
	@DisplayName("활성 연결 수 조회 테스트")
	void getActiveConnectionCountTest() {
		// Given
		int expectedCount = 5;
		when(emitterRepository.getActiveConnectionCount()).thenReturn(expectedCount);

		// When
		int result = alarmEventService.getActiveConnectionCount();

		// Then
		assertEquals(expectedCount, result);
		verify(emitterRepository, times(1)).getActiveConnectionCount();
	}
}