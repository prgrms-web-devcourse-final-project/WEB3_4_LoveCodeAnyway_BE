package com.ddobang.backend.domain.party.service.example;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.party.event.PartyCreatedEvent;
import com.ddobang.backend.global.event.EventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 파티 서비스 예시 - 이벤트 기반 알림 시스템 구현 예시
 * 실제 파티 서비스에서는 이 코드를 참고하여 이벤트 발행 로직을 추가합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PartyServiceExample {
    
    private final EventPublisher eventPublisher;
    
    /**
     * 파티 생성 메소드 예시
     * 파티 저장 후 이벤트를 발행합니다.
     */
    @Transactional
    public void createParty(Long partyId, String partyTitle, List<String> keywords) {
        // 1. 파티 저장 로직 (실제 구현 시 추가)
        log.info("파티 저장: 제목={}, 키워드={}", partyTitle, keywords);
        
        // 2. 이벤트 발행
        PartyCreatedEvent event = new PartyCreatedEvent(partyId, partyTitle, keywords);
        eventPublisher.publish(event);
        
        log.info("파티 생성 완료 및 이벤트 발행: partyId={}", partyId);
    }
}
