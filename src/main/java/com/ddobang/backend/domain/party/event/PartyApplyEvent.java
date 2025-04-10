package com.ddobang.backend.domain.party.event;

import com.ddobang.backend.global.event.DomainEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyApplyEvent implements DomainEvent {
	private Long partyId;
	private String partyTitle;
	private Long hostId;        // 모임장 ID (알림 수신자)
	private Long applicantId;   // 신청자 ID
	private String applicantNickname; // 신청자 닉네임

	@Override
	public String getEventType() {
		return "PARTY_APPLY_EVENT";
	}
}