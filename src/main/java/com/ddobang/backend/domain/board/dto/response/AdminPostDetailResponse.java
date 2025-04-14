package com.ddobang.backend.domain.board.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ddobang.backend.domain.board.dto.PostReplyDto;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.types.PostType;

public record AdminPostDetailResponse(
	Long id,
	PostType type,
	String title,
	String content,
	List<AdminAttachmentResponse> attachments,
	List<PostReplyDto> replies,
	LocalDateTime createdAt
) {
	public static AdminPostDetailResponse from(Post post) {
		return new AdminPostDetailResponse(
			post.getId(),
			post.getType(),
			post.getTitle(),
			post.getContent(),
			post.getAttachments().stream().map(AdminAttachmentResponse::from).toList(),
			post.isAnswered() ? post.getReplies().stream().map(PostReplyDto::from).toList() : null,
			post.getCreatedAt()
		);
	}
}
