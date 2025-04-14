package com.ddobang.backend.domain.party.types;

public enum PartyStatus {
	RECRUITING,   // 모집 중
	FULL,       // 정원 마감
	PENDING,    // 모집 마감
	COMPLETED,    // 모임 진행 완료
	CANCELLED     // 모임 미진행 완료
}
