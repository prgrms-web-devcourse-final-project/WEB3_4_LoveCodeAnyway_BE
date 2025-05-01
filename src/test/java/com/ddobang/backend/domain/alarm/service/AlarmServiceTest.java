package com.ddobang.backend.domain.alarm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest;
import com.ddobang.backend.domain.alarm.dto.response.AlarmCountResponse;
import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.exception.AlarmErrorCode;
import com.ddobang.backend.domain.alarm.exception.AlarmException;
import com.ddobang.backend.domain.alarm.repository.AlarmRepository;

@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {

	@Mock
	private AlarmRepository alarmRepository;

	@InjectMocks
	private AlarmService alarmService;

	private Long userId;
	private AlarmCreateRequest createRequest;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		userId = 1L;
		now = LocalDateTime.now();

		// 테스트용 AlarmCreateRequest 객체 생성
		createRequest = AlarmCreateRequest.builder()
			.receiverId(userId)
			.title("새 알림 제목")
			.content("새 알림 내용")
			.alarmType(AlarmType.SYSTEM)
			.relId(200L)
			.build();
	}

	@Test
	@DisplayName("알림 상세 조회 성공 테스트")
	void getAlarmSuccessTest() {
		// Given
		Long alarmId = 1L;
		Alarm alarm = mock(Alarm.class);
		when(alarm.getId()).thenReturn(1L);
		when(alarm.getReceiverId()).thenReturn(userId);
		when(alarm.getTitle()).thenReturn("테스트 알림 제목");
		when(alarm.getContent()).thenReturn("테스트 알림 내용");
		when(alarm.getAlarmType()).thenReturn(AlarmType.SYSTEM);
		when(alarm.getReadStatus()).thenReturn(false);
		when(alarm.getCreatedAt()).thenReturn(now);
		when(alarm.getModifiedAt()).thenReturn(now);

		when(alarmRepository.findByIdAndReceiverId(alarmId, userId))
			.thenReturn(Optional.of(alarm));

		// When
		AlarmResponse result = alarmService.getAlarm(alarmId, userId);

		// Then
		assertNotNull(result);
		assertEquals(alarm.getId(), result.getId());
		assertEquals(alarm.getTitle(), result.getTitle());
		assertEquals(alarm.getContent(), result.getContent());
		verify(alarmRepository, times(1)).findByIdAndReceiverId(alarmId, userId);
	}

	@Test
	@DisplayName("알림 상세 조회 실패 테스트 - 알림 없음")
	void getAlarmNotFoundTest() {
		// Given
		Long alarmId = 999L;
		when(alarmRepository.findByIdAndReceiverId(alarmId, userId))
			.thenReturn(Optional.empty());

		// When & Then
		AlarmException exception = assertThrows(AlarmException.class, () -> {
			alarmService.getAlarm(alarmId, userId);
		});
		assertEquals(AlarmErrorCode.ALARM_NOT_FOUND, exception.getErrorCode());
		verify(alarmRepository, times(1)).findByIdAndReceiverId(alarmId, userId);
	}

	@Test
	@DisplayName("알림 개수 조회 테스트")
	void getAlarmCountsTest() {
		// Given
		long unreadCount = 5;
		long totalCount = 10;

		when(alarmRepository.countByReceiverIdAndReadStatus(userId, false))
			.thenReturn(unreadCount);

		Page<Alarm> emptyPage = mock(Page.class);
		when(emptyPage.getTotalElements()).thenReturn(totalCount);

		when(alarmRepository.findByReceiverIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class)))
			.thenReturn(emptyPage);

		// When
		AlarmCountResponse result = alarmService.getAlarmCounts(userId);

		// Then
		assertNotNull(result);
		assertEquals(totalCount, result.getTotalCount());
		assertEquals(unreadCount, result.getUnreadCount());
		verify(alarmRepository, times(1)).countByReceiverIdAndReadStatus(userId, false);
		verify(alarmRepository, times(1)).findByReceiverIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class));
	}

	@Test
	@DisplayName("알림 생성 테스트")
	void createAlarmTest() {
		// Given
		Alarm alarm = mock(Alarm.class);
		when(alarm.getId()).thenReturn(1L);
		when(alarm.getTitle()).thenReturn("테스트 알림 제목");
		when(alarm.getContent()).thenReturn("테스트 알림 내용");

		when(alarmRepository.save(any(Alarm.class))).thenReturn(alarm);

		// When
		AlarmResponse result = alarmService.createAlarm(createRequest);

		// Then
		assertNotNull(result);
		assertEquals(alarm.getId(), result.getId());
		assertEquals(alarm.getTitle(), result.getTitle());
		assertEquals(alarm.getContent(), result.getContent());
		verify(alarmRepository, times(1)).save(any(Alarm.class));
	}

	@Test
	@DisplayName("알림 읽음 처리 성공 테스트")
	void markAsReadSuccessTest() {
		// Given
		Long alarmId = 1L;
		Alarm alarm = mock(Alarm.class);

		// 읽음 상태 변경을 위한 설정
		when(alarm.getReadStatus()).thenReturn(false).thenReturn(true);
		doNothing().when(alarm).markAsRead();

		when(alarmRepository.findByIdAndReceiverId(alarmId, userId))
			.thenReturn(Optional.of(alarm));

		// When
		AlarmResponse result = alarmService.markAsRead(alarmId, userId);

		// Then
		assertNotNull(result);
		verify(alarmRepository, times(1)).findByIdAndReceiverId(alarmId, userId);
		verify(alarm, times(1)).markAsRead();
	}

	@Test
	@DisplayName("알림 읽음 처리 테스트 - 이미 읽은 경우")
	void markAsReadAlreadyReadTest() {
		// Given
		Long alarmId = 1L;
		Alarm alarm = mock(Alarm.class);

		// 이미 읽은 상태로 설정
		when(alarm.getReadStatus()).thenReturn(true);

		when(alarmRepository.findByIdAndReceiverId(alarmId, userId))
			.thenReturn(Optional.of(alarm));

		// When
		AlarmResponse result = alarmService.markAsRead(alarmId, userId);

		// Then
		assertNotNull(result);
		verify(alarmRepository, times(1)).findByIdAndReceiverId(alarmId, userId);
		// 이미 읽은 상태면 markAsRead() 메서드가 호출되지 않아야 함
		verify(alarm, never()).markAsRead();
	}

	@Test
	@DisplayName("모든 알림 읽음 처리 테스트")
	void markAllAsReadTest() {
		// Given
		int updatedCount = 5;
		when(alarmRepository.markAllAsReadByReceiverId(userId)).thenReturn(updatedCount);

		// When
		int result = alarmService.markAllAsRead(userId);

		// Then
		assertEquals(updatedCount, result);
		verify(alarmRepository, times(1)).markAllAsReadByReceiverId(userId);
	}

	@Test
	@DisplayName("알림 삭제 성공 테스트")
	void deleteAlarmSuccessTest() {
		// Given
		Long alarmId = 1L;
		Alarm alarm = mock(Alarm.class);

		when(alarmRepository.findByIdAndReceiverId(alarmId, userId))
			.thenReturn(Optional.of(alarm));
		doNothing().when(alarmRepository).delete(any(Alarm.class));

		// When
		alarmService.deleteAlarm(alarmId, userId);

		// Then
		verify(alarmRepository, times(1)).findByIdAndReceiverId(alarmId, userId);
		verify(alarmRepository, times(1)).delete(any(Alarm.class));
	}

	@Test
	@DisplayName("알림 삭제 실패 테스트 - 알림 없음")
	void deleteAlarmNotFoundTest() {
		// Given
		Long alarmId = 999L;
		when(alarmRepository.findByIdAndReceiverId(alarmId, userId))
			.thenReturn(Optional.empty());

		// When & Then
		AlarmException exception = assertThrows(AlarmException.class, () -> {
			alarmService.deleteAlarm(alarmId, userId);
		});
		assertEquals(AlarmErrorCode.ALARM_NOT_FOUND, exception.getErrorCode());
		verify(alarmRepository, times(1)).findByIdAndReceiverId(alarmId, userId);
		verify(alarmRepository, never()).delete(any(Alarm.class));
	}
}