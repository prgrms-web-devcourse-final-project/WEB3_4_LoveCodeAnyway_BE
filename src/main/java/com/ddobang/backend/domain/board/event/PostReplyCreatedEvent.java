package com.ddobang.backend.domain.board.event;

import com.ddobang.backend.global.event.DomainEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostReplyCreatedEvent implements DomainEvent {
	private Long postId;
	private String postTitle;
	private Long postOwnerId;  // 문의글 작성자 ID (알림 수신자)
	// private Long replyId; // TODO: 관리자 아이디 불필요할시 삭제
	private String replyContent;

	@Override
	public String getEventType() {
		return "POST_REPLY_CREATED_EVENT";
	}
}