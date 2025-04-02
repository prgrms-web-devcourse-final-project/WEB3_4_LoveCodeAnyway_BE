package com.ddobang.backend.domain.alarm.infra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ddobang.backend.domain.alarm.exception.SseException;

class EmitterRepositoryTest {

	private EmitterRepository emitterRepository;

	@Mock
	private SseEmitter mockEmitter;

	// 테스트용 사용자 ID
	private static final Long TEST_USER_ID = 1L;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		emitterRepository = new EmitterRepository();
	}

	@Test
	@DisplayName("SseEmitter 저장 및 조회 테스트")
	void saveAndGet_ShouldStoreAndRetrieveEmitter() {
		// when
		SseEmitter result = emitterRepository.save(TEST_USER_ID, mockEmitter);
		SseEmitter retrieved = emitterRepository.get(TEST_USER_ID);

		// then
		assertEquals(mockEmitter, result);
		assertEquals(mockEmitter, retrieved);
	}

	@Test
	@DisplayName("SseEmitter 제거 테스트")
	void remove_ShouldRemoveEmitter() {
		// given
		emitterRepository.save(TEST_USER_ID, mockEmitter);

		// when
		emitterRepository.remove(TEST_USER_ID);

		// then
		assertNull(emitterRepository.get(TEST_USER_ID));
	}

	@Test
	@DisplayName("사용자에게 이벤트 전송 성공 테스트")
	void sendToUser_WhenEmitterExists_ShouldSendEvent() throws IOException {
		// given
		emitterRepository.save(TEST_USER_ID, mockEmitter);
		String eventData = "Test Data";

		// when
		emitterRepository.sendToUser(TEST_USER_ID, eventData, "testEvent", "1");

		// then
		verify(mockEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
	}

	@Test
	@DisplayName("이벤트 전송 실패 시 예외 발생 테스트")
	void sendToUser_WhenIOExceptionOccurs_ShouldThrowSseException() throws IOException {
		// given
		emitterRepository.save(TEST_USER_ID, mockEmitter);
		doThrow(IOException.class).when(mockEmitter).send(any(SseEmitter.SseEventBuilder.class));

		// when & then
		assertThrows(SseException.class, () -> {
			emitterRepository.sendToUser(TEST_USER_ID, "Test Data", "testEvent", "1");
		});

		// 예외 발생 시 emitter 제거 확인
		assertNull(emitterRepository.get(TEST_USER_ID));
	}

	@Test
	@DisplayName("활성 연결 수 조회 테스트")
	void getActiveConnectionCount_ShouldReturnCorrectCount() {
		// given
		assertEquals(0, emitterRepository.getActiveConnectionCount());

		// when
		emitterRepository.save(1L, mockEmitter);
		emitterRepository.save(2L, mockEmitter);
		emitterRepository.save(3L, mockEmitter);

		// then
		assertEquals(3, emitterRepository.getActiveConnectionCount());

		// when - 하나 제거
		emitterRepository.remove(2L);

		// then
		assertEquals(2, emitterRepository.getActiveConnectionCount());
	}
}