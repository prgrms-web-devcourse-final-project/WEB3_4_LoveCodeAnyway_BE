package com.ddobang.backend.domain.board.service;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.entity.PostReply;
import com.ddobang.backend.domain.board.exception.BoardErrorCode;
import com.ddobang.backend.domain.board.exception.BoardException;
import com.ddobang.backend.domain.member.entity.Member;

@Service
public class BoardValidationService {

	// for user
	public void validateWriter(Post post, Member actor) {
		if (!actor.equals(post.getMember())) {
			throw new BoardException(BoardErrorCode.POST_ACCESS_DENIED);
		}
	}

	// for admin
	public void validatePostReplyRelation(Long postId, PostReply reply) {
		if (!reply.getPost().getId().equals(postId)) {
			throw new BoardException(BoardErrorCode.POST_REPLY_INVALID_RELATION);
		}
	}

	public void validateDeletePost(Post post) {
		if (!post.isDeleted()) {
			throw new BoardException(BoardErrorCode.CANNOT_DELETE_NOT_SOFT_DELETED_POST);
		}

		if (!post.getReplies().isEmpty()) {
			throw new BoardException(BoardErrorCode.POST_HAS_REPLIES);
		}
	}
}
