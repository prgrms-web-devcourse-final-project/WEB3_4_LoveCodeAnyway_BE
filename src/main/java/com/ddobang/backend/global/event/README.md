# 이벤트 기반 알림 시스템 사용 가이드

## 개요

이 패키지는 여러 도메인(Party, Message, Theme 등)에서 공통으로 사용할 수 있는 이벤트 기반 알림 시스템을 구현합니다. 도메인 간의 결합도를 낮추고 확장성을 높이기 위해 Spring의 이벤트 시스템을 활용합니다.

## 주요 컴포넌트

- **DomainEvent**: 모든 도메인 이벤트는 이 인터페이스를 구현해야 합니다.
- **EventPublisher**: 도메인 이벤트를 발행하는 유틸리티 클래스입니다.

## 사용 방법

### 1. 도메인 이벤트 정의

새로운 도메인 이벤트를 정의하려면 `DomainEvent` 인터페이스를 구현하는 클래스를 작성합니다.

```java
// 파티 생성 이벤트 예시
package com.ddobang.backend.domain.party.event;

import com.ddobang.backend.global.event.DomainEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class PartyCreatedEvent implements DomainEvent {
    private final Long partyId;
    private final String partyTitle;
    private final List<String> keywords;
    
    public PartyCreatedEvent(Long partyId, String partyTitle, List<String> keywords) {
        this.partyId = partyId;
        this.partyTitle = partyTitle;
        this.keywords = keywords;
    }
    
    @Override
    public String getEventType() {
        return "PARTY_CREATED";
    }
}
```

### 2. 도메인 서비스에서 이벤트 발행

도메인 서비스에서 `EventPublisher`를 주입받아 이벤트를 발행합니다.

```java
// 파티 서비스 예시
@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;
    private final EventPublisher eventPublisher;
    
    @Transactional
    public Party createParty(Party party) {
        // 파티 저장
        Party savedParty = partyRepository.save(party);
        
        // 이벤트 발행
        List<String> keywords = savedParty.getKeywords().stream()
            .map(PartyKeyword::getKeyword)
            .collect(Collectors.toList());
            
        PartyCreatedEvent event = new PartyCreatedEvent(
            savedParty.getId(),
            savedParty.getTitle(),
            keywords
        );
        eventPublisher.publish(event);
        
        return savedParty;
    }
}
```

### 3. 이벤트 리스너 구현

알림 서비스에서 도메인 이벤트를 구독하여 처리하는 리스너를 구현합니다.
이는 `AlarmEventListener` 클래스에 구현되어 있으며, 새로운 이벤트 타입에 대한 처리를 추가할 수 있습니다.

```java
// 파티 이벤트 리스너 메서드 예시
@Async("notificationTaskExecutor")
@TransactionalEventListener
public void handlePartyCreatedEvent(PartyCreatedEvent event) {
    log.info("파티 생성 이벤트 수신: partyId={}, 제목={}", event.getPartyId(), event.getPartyTitle());
    
    // 키워드 구독자 조회 및 알림 생성 로직
    // ...
}
```

## 주의사항

1. 모든 도메인 이벤트는 불변(immutable) 객체로 구현합니다.
2. 이벤트 처리는 비동기(`@Async`)로 수행되기 때문에, 트랜잭션 관리에 주의해야 합니다.
3. 이벤트 핸들러에서 발생한 예외는 적절히 처리해야 원래 트랜잭션에 영향을 주지 않습니다.

## 더 자세한 정보

더 자세한 구현 방법 및 아키텍처 설명은 다음 문서를 참조하세요:
- [이벤트 기반 알림 시스템 설계 및 구현 가이드](../../../../../../resources/docs/event-based-notification-system.md)
