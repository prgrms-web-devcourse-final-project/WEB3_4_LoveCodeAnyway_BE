package com.ddobang.backend.domain.alarm.entity;

public enum AlarmType {
	SYSTEM,       // 시스템 알림 - 관리자 알림 전부
	MESSAGE,      // 쪽지 알림
	FOLLOW,       // 구독한 테마 모임 알림
	OTHER         // 기타 알림
}