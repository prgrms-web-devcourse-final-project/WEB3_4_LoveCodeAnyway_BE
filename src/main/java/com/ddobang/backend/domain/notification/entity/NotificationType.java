package com.ddobang.backend.domain.notification.entity;

public enum NotificationType {
	SYSTEM,       // 시스템 알림
	COMMENT,      // 댓글 알림
	MENTION,      // 언급 알림
	FOLLOW,       // 팔로우 알림
	EVENT         // 이벤트 알림
}