package com.ddobang.backend.domain.notification.entity;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Notification extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@Column(name = "receiver_id", nullable = false)
	private Long receiverId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "read_status", nullable = false)
	private Boolean readStatus;

	@Column(name = "rel_id")
	private Long relId;

	@Builder
	public Notification(Long receiverId, String name, String content, Long relId) {
		this.receiverId = receiverId;
		this.name = name;
		this.content = content;
		this.readStatus = false; // 기본값 읽지 않음(false)
		this.relId = relId;
	}

	// 읽음 상태 변경 메서드
	public void markAsRead() {
		this.readStatus = true;
	}
}
