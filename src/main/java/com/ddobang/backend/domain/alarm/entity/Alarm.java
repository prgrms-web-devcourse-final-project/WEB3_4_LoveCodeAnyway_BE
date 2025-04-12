package com.ddobang.backend.domain.alarm.entity;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Alarm extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "receiver_id", nullable = false)
	private Member receiver;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "read_status", nullable = false)
	private Boolean readStatus;

	@Column(name = "rel_id")
	private Long relId;

	@Enumerated(EnumType.STRING)
	@Column(name = "alarm_type", nullable = false)
	private AlarmType alarmType;

	@Builder
	public Alarm(Member receiver, String title, String content,
		AlarmType alarmType, Long relId) {
		this.receiver = receiver;
		this.title = title;
		this.content = content;
		this.readStatus = false; // 기본값 읽지 않음(false)
		this.alarmType = alarmType;
		this.relId = relId;
	}

	// 읽음 상태 변경 메서드
	public void markAsRead() {
		this.readStatus = true;
	}
}