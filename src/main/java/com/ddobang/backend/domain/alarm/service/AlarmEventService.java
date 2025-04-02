package com.ddobang.backend.domain.alarm.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.exception.AlarmErrorCode;
import com.ddobang.backend.domain.alarm.exception.SseException;
import com.ddobang.backend.domain.alarm.infra.EmitterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmEventService {
	private final EmitterRepository emitterRepository;
	@Value("${custom.sse.timeout}")
	private Long sseTimeout;

	// SSE 연결 수립 (구독)
	public SseEmitter subscribe(Long userId) {
		log.info("사용자 {}의 SSE 구독 시작", userId);

		// 기존 연결이 있으면 제거
		emitterRepository.remove(userId);

		// 새 이미터 생성 (1시간 타임아웃)
		SseEmitter emitter = new SseEmitter(sseTimeout);

		// 완료, 타임아웃, 에러 발생 시 이미터 제거 및 로깅
		emitter.onCompletion(() -> {
			log.info("사용자 {}의 SSE 연결 완료", userId);
			emitterRepository.remove(userId);
		});

		emitter.onTimeout(() -> {
			log.warn("사용자 {}의 SSE 연결 타임아웃", userId);
			emitterRepository.remove(userId);
			throw new SseException(AlarmErrorCode.SSE_TIMEOUT);
		});

		emitter.onError((e) -> {
			log.error("사용자 {}의 SSE 연결 오류: {}", userId, e.getMessage());
			emitterRepository.remove(userId);
			if (e instanceof IOException) {
				throw new SseException(AlarmErrorCode.SSE_CONNECTION_ERROR, (IOException)e);
			}
		});

		// 이미터 저장
		emitterRepository.save(userId, emitter);

		// 연결 성공 이벤트 전송
		try {
			emitter.send(SseEmitter.event()
				.id("connect")
				.name("connect")
				.data("연결이 성공적으로 수립되었습니다."));
			log.info("사용자 {}에게 SSE 연결 확인 이벤트 전송", userId);
		} catch (IOException e) {
			log.error("사용자 {}에게 SSE 연결 확인 이벤트 전송 실패: {}", userId, e.getMessage());
			emitterRepository.remove(userId);
			throw new SseException(AlarmErrorCode.SSE_CONNECTION_ERROR, e);
		}

		return emitter;
	}

	// 알림 이벤트 전송
	public void sendNotification(Long userId, AlarmResponse alarm) {
		log.info("사용자 {}에게 알림 전송 시도: {}", userId, alarm.getTitle());
		try {
			emitterRepository.sendToUser(
				userId,
				alarm,
				"alarm",
				alarm.getId().toString()
			);
		} catch (SseException e) {
			log.error("알림 전송 중 오류 발생: {}", e.getMessage());
			// 여기서는 예외를 전파하지 않고 로깅만 수행
			// 알림 전송 실패가 비즈니스 로직 전체를 중단시키지 않도록 함
		}
	}

	// 활성 연결 수 조회 (모니터링용)
	public int getActiveConnectionCount() {
		return emitterRepository.getActiveConnectionCount();
	}
}