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

	// 메시지에 접근 권한이 있는지 확인
	// 보낸 사람이거나 받은 사람인 경우에만 접근 가능
	// @param member 권한 확인할 사용자
	public boolean hasAccessPermission(Member member) {
		return isSender(member) || isReceiver(member);
	}

	// 메시지의 발신자인지 확인
	// @param member 확인할 사용자
	public boolean isSender(Member member) {
		return this.sender.getId().equals(member.getId());
	}

	// 메시지의 수신자인지 확인
	// @param member 확인할 사용자
	public boolean isReceiver(Member member) {
		return this.receiver.getId().equals(member.getId());
	}

	// 읽음 상태 변경 권한이 있는지 확인
	// 받은 사람만 읽음 상태 변경 가능
	// @param member 권한 확인할 사용자
	public boolean hasReadPermission(Member member) {
		return isReceiver(member);
	}

	// 삭제 권한이 있는지 확인
	// 받은 사람만 삭제 가능
	// @param member 권한 확인할 사용자
	public boolean hasDeletePermission(Member member) {
		return isReceiver(member);
	}
}