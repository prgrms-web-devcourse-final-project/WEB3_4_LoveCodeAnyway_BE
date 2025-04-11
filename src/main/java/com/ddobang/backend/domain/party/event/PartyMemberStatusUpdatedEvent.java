package com.ddobang.backend.domain.party.event;

import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.global.event.DomainEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyMemberStatusUpdatedEvent implements DomainEvent {
	private Long partyId;
	private String partyTitle;
	private Long memberId;       // 신청자 ID (알림 수신자)
	private Long hostId;         // 모임장 ID
	private String hostNickname; // 모임장 닉네임
	private PartyMemberStatus newStatus; // 변경된 상태 (ACCEPTED, CANCELLED)

	@Override
	public String getEventType() {
		return "PARTY_MEMBER_STATUS_UPDATED_EVENT";
	}
}