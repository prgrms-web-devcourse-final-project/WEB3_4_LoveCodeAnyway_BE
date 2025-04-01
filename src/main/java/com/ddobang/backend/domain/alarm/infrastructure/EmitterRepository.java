package com.ddobang.backend.domain.alarm.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class EmitterRepository {
	// 사용자 ID를 키로, SseEmitter를 값으로 저장하는 맵
	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	// 새로운 SseEmitter 등록
	public SseEmitter save(Long userId, SseEmitter emitter) {
		emitters.put(userId, emitter);
		return emitter;
	}

	// 특정 사용자의 SseEmitter 조회
	public SseEmitter get(Long userId) {
		return emitters.get(userId);
	}

	// 특정 사용자의 SseEmitter 제거
	public void remove(Long userId) {
		emitters.remove(userId);
	}
}
