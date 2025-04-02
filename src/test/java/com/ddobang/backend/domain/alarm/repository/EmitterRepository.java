package com.ddobang.backend.domain.alarm.infra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ddobang.backend.domain.alarm.exception.SseException;

class EmitterRepositoryTest {

	private EmitterRepository emitterRepository;

	@Mock
	private SseEmitter mockEmitter;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		emitterRepository = new EmitterRepository();
	}

	@Test
	void saveAndGet_ShouldStoreAndRetrieveEmitter() {
		// given
		Long userId = 1L;

		// when
		SseEmitter result = emitterRepository.save(userId, mockEmitter);
		SseEmitter retrieved = emitterRepository.get(userId);

		// then
		assertEquals(mockEmitter, result);
		assertEquals(mockEmitter, retrieved);
	}

	@Test
	void remove_ShouldRemoveEmitter() {
		// given
		Long userId = 1L;
		emitterRepository.save(userId, mockEmitter);

		// when
		emitterRepository.remove(userId);

		// then
		assertNull(emitterRepository.get(userId));
	}

	@Test
	void sendToUser_WhenEmitterExists_ShouldSendEvent() throws IOException {
		// given
		Long userId = 1L;
		emitterRepository.save(userId, mockEmitter);
		String eventData = "Test Data";

		// when
		emitterRepository.sendToUser(userId, eventData, "testEvent", "1");

		// then
		verify(mockEmitter, times(1)).send(any());
	}

	@Test
	void sendToUser_WhenIOExceptionOccurs_ShouldThrowSseException() throws IOException {
		// given
		Long userId = 1L;
		emitterRepository.save(userId, mockEmitter);
		doThrow(IOException.class).when(mockEmitter).send(any());

		// when & then
		assertThrows(SseException.class, () -> {
			emitterRepository.sendToUser(userId, "Test Data", "testEvent", "1");
		});

		// 예외 발생 시 emitter 제거 확인
		assertNull(emitterRepository.get(userId));
	}

	@Test
	void getActiveConnectionCount_ShouldReturnCorrectCount() {
		// given
		assertEquals(0, emitterRepository.getActiveConnectionCount());

		// when
		emitterRepository.save(1L, mockEmitter);
		emitterRepository.save(2L, mockEmitter);

		// then
		assertEquals(2, emitterRepository.getActiveConnectionCount());
	}
}