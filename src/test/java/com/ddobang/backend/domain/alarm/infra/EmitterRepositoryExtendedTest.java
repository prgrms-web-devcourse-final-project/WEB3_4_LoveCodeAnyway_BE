package com.ddobang.backend.domain.alarm.infra;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class EmitterRepositoryExtendedTest {

	private EmitterRepository emitterRepository;

	@Mock
	private SseEmitter mockEmitter1;

	@Mock
	private SseEmitter mockEmitter2;

	@Mock
	private SseEmitter mockEmitter3;

	// 테스트용 사용자 ID들
	private static final Long USER_ID_1 = 1L;
	private static final Long USER_ID_2 = 2L;
	private static final Long USER_ID_3 = 3L;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		emitterRepository = new EmitterRepository();
	}

	@Test
	@DisplayName("다중 사용자에 대한 SseEmitter 관리 테스트")
	void multipleUsers_ShouldManageEmittersCorrectly() {
		// 여러 사용자의 이미터 저장
		emitterRepository.save(USER_ID_1, mockEmitter1);
		emitterRepository.save(USER_ID_2, mockEmitter2);
		emitterRepository.save(USER_ID_3, mockEmitter3);

		// 저장 확인
		assertEquals(3, emitterRepository.getActiveConnectionCount());
		assertEquals(mockEmitter1, emitterRepository.get(USER_ID_1));
		assertEquals(mockEmitter2, emitterRepository.get(USER_ID_2));
		assertEquals(mockEmitter3, emitterRepository.get(USER_ID_3));

		// 한 사용자의 이미터 제거
		emitterRepository.remove(USER_ID_2);

		// 제거 확인
		assertEquals(2, emitterRepository.getActiveConnectionCount());
		assertNull(emitterRepository.get(USER_ID_2));

		// 남은 사용자들의 이미터 확인
		assertEquals(mockEmitter1, emitterRepository.get(USER_ID_1));
		assertEquals(mockEmitter3, emitterRepository.get(USER_ID_3));
	}

	@Test
	@DisplayName("동일 사용자의 이미터 업데이트 테스트")
	void sameUser_ShouldUpdateEmitter() {
		// 첫 번째 이미터 저장
		emitterRepository.save(USER_ID_1, mockEmitter1);
		assertEquals(mockEmitter1, emitterRepository.get(USER_ID_1));

		// 동일 사용자에 대해 두 번째 이미터 저장 (업데이트)
		emitterRepository.save(USER_ID_1, mockEmitter2);

		// 업데이트 확인
		assertEquals(mockEmitter2, emitterRepository.get(USER_ID_1));
		assertEquals(1, emitterRepository.getActiveConnectionCount());
	}

	@Test
	@DisplayName("동시성 환경에서 이미터 관리 테스트")
	void concurrentAccess_ShouldHandleEmittersCorrectly() throws InterruptedException {
		// 동시에 접근하는 스레드 수
		int threadCount = 10;

		// 테스트용 CountDownLatch
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch finishLatch = new CountDownLatch(threadCount);

		// 여러 스레드에서 동시에 이미터 저장 시도
		for (int i = 0; i < threadCount; i++) {
			final long userId = i + 1;
			new Thread(() -> {
				try {
					// 모든 스레드가 동시에 시작하도록 대기
					startLatch.await();

					// 이미터 저장
					SseEmitter emitter = new SseEmitter();
					emitterRepository.save(userId, emitter);

					// 작업 완료 알림
					finishLatch.countDown();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}).start();
		}

		// 모든 스레드 시작
		startLatch.countDown();

		// 모든 스레드가 완료될 때까지 대기 (최대 5초)
		boolean completed = finishLatch.await(5, TimeUnit.SECONDS);
		assertTrue(completed, "모든 스레드가 정상적으로 완료되지 않았습니다.");

		// 저장된 이미터 수 확인
		assertEquals(threadCount, emitterRepository.getActiveConnectionCount());
	}

	@Test
	@DisplayName("존재하지 않는 사용자에게 이벤트 전송 시 예외 발생 테스트")
	void sendToNonExistingUser_ShouldNotThrowException() {
		// 존재하지 않는 사용자 ID
		Long nonExistingUserId = 999L;

		// 이벤트 전송 시도 시 예외가 발생하지 않아야 함
		assertDoesNotThrow(() -> {
			emitterRepository.sendToUser(nonExistingUserId, "Test Data", "testEvent", "1");
		});
	}

	@Test
	@DisplayName("다양한 타입의 데이터 전송 테스트")
	void sendDifferentDataTypes_ShouldHandleCorrectly() throws IOException {
		// 이미터 저장
		emitterRepository.save(USER_ID_1, mockEmitter1);

		// 다양한 데이터 타입 전송 테스트
		// 1. 문자열
		emitterRepository.sendToUser(USER_ID_1, "String data", "stringEvent", "1");

		// 2. 숫자
		emitterRepository.sendToUser(USER_ID_1, 123, "numberEvent", "2");

		// 3. 객체
		TestData testData = new TestData("Test", 100);
		emitterRepository.sendToUser(USER_ID_1, testData, "objectEvent", "3");

		// 전송 확인
		verify(mockEmitter1, times(3)).send(any(SseEmitter.SseEventBuilder.class));
	}

	// 테스트용 데이터 클래스
	private static class TestData {
		private String name;
		private int value;

		public TestData(String name, int value) {
			this.name = name;
			this.value = value;
		}

		// Getter 및 Setter (Jackson 직렬화를 위해 필요)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}
}