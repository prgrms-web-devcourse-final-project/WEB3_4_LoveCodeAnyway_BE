package com.ddobang.backend.domain.party.event;

import java.util.List;

import com.ddobang.backend.global.event.DomainEvent;

import lombok.Getter;

/**
 * 파티 생성 이벤트
 * 새로운 파티가 생성되었을 때 발생하는 이벤트입니다.
 */
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
