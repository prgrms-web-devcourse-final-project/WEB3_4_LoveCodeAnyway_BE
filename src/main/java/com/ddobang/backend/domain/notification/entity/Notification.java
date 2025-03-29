package com.ddobang.backend.domain.notification.entity;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;

@Entity
public class Notification extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 500)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType type;

	private String targetUrl;

	@Column(nullable = false)
	private boolean isRead;

	@Builder
	public Notification(Long userId, String title, String content, NotificationType type, String targetUrl) {
		this.userId = userId;
		this.title = title;
		this.content = content;
		this.type = type;
		this.targetUrl = targetUrl;
		this.isRead = false;
	}

	public void markAsRead() {
		this.isRead = true;
	}
}
