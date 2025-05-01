package com.ddobang.backend.domain.party.types;

public enum PartyMemberStatus {
	APPLICANT,    // 참여 신청 중
	CANCELLED,    // 참여 신청 취소
	ACCEPTED,    // 참여 승인
	REJECTED,    // 참여 거절 (마감)
}
