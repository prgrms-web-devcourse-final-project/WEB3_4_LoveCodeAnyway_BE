# 이벤트 기반 알림 시스템 설계 및 구현 가이드

## 1. 개요

본 문서는 이벤트 기반 알림 시스템의 설계 및 구현 방법에 대해 설명합니다. 여러 도메인(파티, 메시지, 시스템 공지 등)에서 알림을 통합적으로 처리하기 위한 아키텍처를 제시합니다.

## 2. 아키텍처

### 2.1. 전체 구조

```
[도메인 서비스] --이벤트 발행--> [이벤트 처리 시스템] --> [알림 서비스] --> [SSE 연결]
```

### 2.2. 주요 컴포넌트

- **DomainEvent**: 모든 도메인 이벤트의 기본 인터페이스
- **EventPublisher**: 도메인 이벤트를 발행하는 컴포넌트
- **AlarmEventListener**: 도메인 이벤트를 구독하여 알림을 생성하는 리스너
- **AlarmService**: 알림 생성 및 관리 서비스
- **AlarmEventService**: SSE를 통한 실시간 알림 전송 서비스

## 3. 구현 방법

### 3.1. 이벤트 인터페이스 정의

```java
public interface DomainEvent {
    String getEventType();
}
```

### 3.2. 도메인별 이벤트 클래스 구현

```java
// 메시지 전송 이벤트
public class MessageSentEvent implements DomainEvent {
    private final Long messageId;
    private final Long senderId;
    private final String senderName;
    private final Long receiverId;
    
    // 생성자, getter...
    
    @Override
    public String getEventType() {
        return "MESSAGE_SENT";
    }
}

// 파티 생성 이벤트
public class PartyCreatedEvent implements DomainEvent {
    private final Long partyId;
    private final String partyTitle;
    private final List<String> keywords;
    
    // 생성자, getter...
    
    @Override
    public String getEventType() {
        return "PARTY_CREATED";
    }
}
```

### 3.3. 이벤트 발행자 구현

```java
@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final ApplicationEventPublisher publisher;
    
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
```

### 3.4. 도메인 서비스에서 이벤트 발행

```java
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final EventPublisher eventPublisher;
    
    @Transactional
    public Message sendMessage(Message message) {
        // 메시지 저장
        Message savedMessage = messageRepository.save(message);
        
        // 이벤트 발행
        MessageSentEvent event = new MessageSentEvent(
            savedMessage.getId(),
            savedMessage.getSenderId(),
            savedMessage.getSender().getNickname(),
            savedMessage.getReceiverId()
        );
        eventPublisher.publish(event);
        
        return savedMessage;
    }
}
```

### 3.5. 알림 이벤트 리스너 구현

```java
@Component
@RequiredArgsConstructor
public class AlarmEventListener {
    private final AlarmService alarmService;
    
    @Async
    @TransactionalEventListener
    public void handleMessageSentEvent(MessageSentEvent event) {
        AlarmCreateRequest request = AlarmCreateRequest.builder()
            .receiverId(event.getReceiverId())
            .title("새 메시지 도착")
            .content(event.getSenderName() + "님으로부터 새 메시지가 도착했습니다.")
            .relId(event.getMessageId())
            .alarmType(AlarmType.MESSAGE)
            .build();
        
        alarmService.createAlarm(request);
    }
    
    @Async
    @TransactionalEventListener
    public void handlePartyCreatedEvent(PartyCreatedEvent event) {
        // 키워드 구독자 조회 및 알림 생성 로직
        // ...
    }
}
```

## 4. 확장 방법

### 4.1. 새로운 도메인 이벤트 추가

1. 새로운 도메인 이벤트 클래스 생성
2. 해당 도메인 서비스에서 이벤트 발행 로직 추가
3. `AlarmEventListener`에 새로운 이벤트 핸들러 메서드 추가

### 4.2. 대규모 확장을 위한 메시징 큐 도입

시스템이 확장됨에 따라 Spring의 이벤트 시스템 대신 Kafka 또는 RabbitMQ와 같은 메시징 큐를 도입할 수 있습니다.

## 5. 주의사항

1. **트랜잭션 처리**: `@TransactionalEventListener`를 사용하여 트랜잭션 완료 후 이벤트가 처리되도록 합니다.
2. **비동기 처리**: `@Async`를 통해 이벤트 처리가 비동기적으로 수행되도록 합니다.
3. **오류 처리**: 알림 생성 실패 시 적절한 오류 처리 및 로깅을 구현해야 합니다.
4. **모니터링**: 이벤트 발행 및 처리 과정을 모니터링할 수 있는 지표를 수집합니다.

## 6. 테스트 방법

```java
@SpringBootTest
public class AlarmEventTest {
    @Autowired
    private EventPublisher eventPublisher;
    
    @MockBean
    private AlarmService alarmService;
    
    @Test
    void testMessageSentEventTriggersAlarm() {
        // given
        MessageSentEvent event = new MessageSentEvent(1L, 2L, "홍길동", 3L);
        
        // when
        eventPublisher.publish(event);
        
        // then
        verify(alarmService, timeout(1000)).createAlarm(any(AlarmCreateRequest.class));
    }
}
```

## 7. 확장 계획

### 7.1. 단기 계획
- 추가 도메인(테마, 그룹 등)에 대한 알림 이벤트 구현
- 알림 선호도 설정 기능 추가

### 7.2. 중기 계획
- 메시징 큐 도입을 통한 확장성 개선
- 알림 분석 및 통계 기능 개발

### 7.3. 장기 계획
- 멀티 디바이스 알림 동기화
- 푸시 알림 통합

## 8. 참고 자료

- [Spring Events Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events)
- [Spring @Async Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#scheduling)
