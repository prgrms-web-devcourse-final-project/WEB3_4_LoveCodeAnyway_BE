package com.ddobang.backend.domain.board.dto;

import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.types.PostType;

public record PostDto(
	Long id,
	PostType type,
	String title,
	String content,
	Long memberId,
	String nickName,
	boolean hasAttachment
) {
	public static PostDto from(Post post) {
		return new PostDto(
			post.getId(),
			post.getType(),
			post.getTitle(),
			post.getContent(),
			post.getMember().getId(),
			post.getMember().getNickname(),
			!post.getAttachments().isEmpty()
		);
	}
}
