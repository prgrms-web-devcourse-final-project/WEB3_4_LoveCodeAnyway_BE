package com.ddobang.backend.domain.message.entity;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 보낸 사람과의 연관관계
	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private Member sender;

	// 받는 사람과의 연관관계
	@ManyToOne
	@JoinColumn(name = "receiver_id", nullable = false)
	private Member receiver;

	@Column(nullable = false)
	private String content;

	// true이면 읽은 상태, false이면 읽지 않은 상태
	// Enum 필요 없어보임
	@Column(name = "is_read", nullable = false)
	private boolean isRead;

	@Builder
	public Message(Member sender, Member receiver, String content, boolean isRead) {
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
		this.isRead = isRead;
	}

	public void changeToRead() {
		this.isRead = true;
	}

	// Message.java에 아래 메서드들을 추가
	public boolean canAccess(Member member) {
		return this.sender.getId().equals(member.getId()) ||
			this.receiver.getId().equals(member.getId());
	}

	public boolean canMarkAsRead(Member member) {
		return this.receiver.getId().equals(member.getId());
	}

	public boolean canDelete(Member member) {
		return this.receiver.getId().equals(member.getId());
	}
}
