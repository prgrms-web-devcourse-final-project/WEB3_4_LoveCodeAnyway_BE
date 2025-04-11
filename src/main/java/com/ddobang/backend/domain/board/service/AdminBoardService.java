package com.ddobang.backend.domain.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.board.dto.PostReplyDto;
import com.ddobang.backend.domain.board.dto.request.AdminPostSearchCondition;
import com.ddobang.backend.domain.board.dto.request.PostReplyRequest;
import com.ddobang.backend.domain.board.dto.response.AdminPostDetailResponse;
import com.ddobang.backend.domain.board.dto.response.PostSummaryResponse;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.entity.PostReply;
import com.ddobang.backend.domain.board.event.PostReplyCreatedEvent;
import com.ddobang.backend.domain.board.exception.BoardErrorCode;
import com.ddobang.backend.domain.board.exception.BoardException;
import com.ddobang.backend.domain.board.repository.PostReplyRepository;
import com.ddobang.backend.domain.board.repository.PostRepository;
import com.ddobang.backend.global.event.EventPublisher;
import com.ddobang.backend.global.response.PageDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminBoardService {
	private final PostRepository postRepository;
	private final PostReplyRepository postReplyRepository;
	private final BoardService boardService;
	private final BoardValidationService boardValidationService;
	private final EventPublisher eventPublisher; // 답변 알림을 발행하기 위한 이벤트 퍼블리셔

	public PageDto<PostSummaryResponse> getPostsForAdmin(int page, int size, AdminPostSearchCondition condition) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<PostSummaryResponse> posts = postRepository.findPostsForAdmin(pageable, condition);
		return PageDto.of(posts);
	}

	public AdminPostDetailResponse getPostForAdmin(Long postId) {
		Post post = boardService.getPostById(postId);
		return AdminPostDetailResponse.from(post);
	}

	@Transactional
	public void deletePost(Long postId) {
		Post post = boardService.getPostById(postId);
		boardValidationService.validateDeletePost(post);
		postRepository.delete(post);
	}

	@Transactional
	public PostReplyDto createReply(Long postId, PostReplyRequest request) {
		Post post = boardService.getPostById(postId);

		PostReply reply = PostReply.of(post, request.content());
		postReplyRepository.save(reply);

		post.addReply(reply);

		// 이벤트 발행 - 문의 작성자에게 알림
		eventPublisher.publish(PostReplyCreatedEvent.builder()
			.postId(post.getId())
			.postTitle(post.getTitle())
			.postOwnerId(post.getMember().getId())
			.replyContent(reply.getContent())
			.build());

		return PostReplyDto.from(reply);
	}

	private PostReply getPostReplyById(Long postId, Long replyId) {
		PostReply postReply = postReplyRepository.findById(replyId)
			.orElseThrow(() -> new BoardException(BoardErrorCode.POST_REPLY_NOT_FOUND));
		boardValidationService.validatePostReplyRelation(postId, postReply);
		return postReply;
	}

	@Transactional
	public PostReplyDto modifyReply(Long postId, Long replyId, PostReplyRequest request) {
		PostReply reply = getPostReplyById(postId, replyId);

		reply.updateContent(request.content());
		return PostReplyDto.from(reply);
	}

	@Transactional
	public void deleteReply(Long postId, Long replyId) {
		PostReply reply = getPostReplyById(postId, replyId);
		postReplyRepository.delete(reply);

		Post post = reply.getPost();
		post.removeReply(reply);
	}
}
