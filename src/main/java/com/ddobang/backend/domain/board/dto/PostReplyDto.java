package com.ddobang.backend.domain.board.dto;

import com.ddobang.backend.domain.board.entity.PostReply;

public record PostReplyDto(
	Long id,
	String content
) {
	public static PostReplyDto from(PostReply postReply) {
		return new PostReplyDto(
			postReply.getId(),
			postReply.getContent()
		);
	}
}
