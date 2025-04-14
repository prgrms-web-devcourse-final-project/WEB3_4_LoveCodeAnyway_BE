package com.ddobang.backend.domain.alarm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest;
import com.ddobang.backend.domain.alarm.dto.response.AlarmCountResponse;
import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.exception.AlarmErrorCode;
import com.ddobang.backend.domain.alarm.exception.AlarmException;
import com.ddobang.backend.domain.alarm.repository.AlarmRepository;
import com.ddobang.backend.global.response.PageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {
	private final AlarmRepository alarmRepository;

	// 사용자 알림 목록 조회
	public PageDto<AlarmResponse> getAlarms(Long userId, Pageable pageable) {
		Page<Alarm> alarms = alarmRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
		return PageDto.of(alarms.map(AlarmResponse::from));
	}

	// 알림 상세조회
	public AlarmResponse getAlarm(Long alarmId, Long userId) {
		Alarm alarm = alarmRepository.findByIdAndReceiverId(alarmId, userId)
			.orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));
		return AlarmResponse.from(alarm);
	}

	// 알림 개수 조회
	public AlarmCountResponse getAlarmCounts(Long userId) {
		long unreadCount = alarmRepository.countByReceiverIdAndReadStatus(userId, false);
		long totalCount = alarmRepository.findByReceiverIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
			.getTotalElements();

		return AlarmCountResponse.of(totalCount, unreadCount);
	}

	// 알림 생성
	@Transactional
	public AlarmResponse createAlarm(AlarmCreateRequest request) {
		Alarm alarm = request.toEntity();
		Alarm savedAlarm = alarmRepository.save(alarm);
		return AlarmResponse.from(savedAlarm);
	}

	//읽음 처리
	@Transactional
	public AlarmResponse markAsRead(Long alarmId, Long userId) {
		Alarm alarm = alarmRepository.findByIdAndReceiverId(alarmId, userId)
			.orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));

		// 이미 읽은 알림 바로 반환
		if (alarm.getReadStatus()) {
			return AlarmResponse.from(alarm);
		}

		alarm.markAsRead();
		return AlarmResponse.from(alarm);
	}

	// 전부 읽음
	@Transactional
	public int markAllAsRead(Long userId) {
		return alarmRepository.markAllAsReadByReceiverId(userId);
	}

	//알림삭제
	@Transactional
	public void deleteAlarm(Long alarmId, Long userId) {
		Alarm alarm = alarmRepository.findByIdAndReceiverId(alarmId, userId)
			.orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));

		alarmRepository.delete(alarm);
	}

	// 알림 리다이렉트 URL 생성
	@Transactional
	public String getRedirectUrl(Long alarmId, Long userId) {
		Alarm alarm = alarmRepository.findByIdAndReceiverId(alarmId, userId)
			.orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));

		// 읽음 처리
		if (!alarm.getReadStatus()) {
			alarm.markAsRead();
		}

		// 알람 타입과 관련 ID에 따라 리다이렉트 URL 생성
		return generateRedirectUrl(alarm.getAlarmType(), alarm.getRelId());
	}

	private String generateRedirectUrl(AlarmType alarmType, Long relId) {
		if (relId == null) {
			return "/notifications"; // 기본 알림 페이지
		}

		switch (alarmType) {
			case MESSAGE -> {
				return "/messages/" + relId;
			}
			case SUBSCRIBE, PARTY_APPLY, PARTY_STATUS -> {
				return "/parties/" + relId;
			}
			case POST_REPLY -> {
				return "/boards/" + relId; // 문의 답변 알림
			}
			default -> {
				return "/alarms";
			}
		}
	}
}
