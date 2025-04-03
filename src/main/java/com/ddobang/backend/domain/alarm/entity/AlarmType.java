package com.ddobang.backend.domain.alarm.entity;

// ALARMTYPE + relId 로 관련 url 연결
public enum AlarmType {
	SYSTEM,       // 시스템 알림 - 관리자 알림 전부
	MESSAGE,      // 쪽지 왔을때 알림
	SUBSCRIBE,    // 키워드 테마/모임 구독후 알림
	OTHER         // 기타 알림
}