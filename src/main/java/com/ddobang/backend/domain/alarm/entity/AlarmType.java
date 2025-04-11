package com.ddobang.backend.domain.alarm.entity;

public enum AlarmType {
	SYSTEM,            // 시스템 알림 - 관리자 알림 전부
	MESSAGE,        // 쪽지 왔을때 알림
	SUBSCRIBE,        // 키워드 테마/모임 구독후 알림
	PARTY_APPLY,    // 모임 신청 알림 (추가)
	PARTY_STATUS,    // 모임 신청 상태 변경 알림 (추가)
	ANSWER_COMMENT, // 문의글에 답변이 달렸을때 알림
	POST_REPLY,    // 문의 답변 알림
	OTHER            // 기타 알림
}