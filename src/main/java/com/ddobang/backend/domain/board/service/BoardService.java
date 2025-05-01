package com.ddobang.backend.domain.board.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.board.dto.PostDto;
import com.ddobang.backend.domain.board.dto.request.PostRequest;
import com.ddobang.backend.domain.board.dto.response.PostDetailResponse;
import com.ddobang.backend.domain.board.dto.response.PostSummaryResponse;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.exception.BoardErrorCode;
import com.ddobang.backend.domain.board.exception.BoardException;
import com.ddobang.backend.domain.board.repository.AttachmentRepository;
import com.ddobang.backend.domain.board.repository.PostRepository;
import com.ddobang.backend.domain.board.types.PostType;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.global.response.PageDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final PostRepository postRepository;
	private final BoardValidationService boardValidationService;
	private final AttachmentRepository attachmentRepository;

	public PageDto<PostSummaryResponse> getMyPosts(PostType type, String keyword, int page, int size, Long id) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<PostSummaryResponse> myPosts = postRepository.findMyPosts(id, type, keyword, pageable);
		return PageDto.of(myPosts);
	}

	public Post getPostById(Long id) {
		return postRepository.findById(id)
			.orElseThrow(() -> new BoardException(BoardErrorCode.POST_NOT_FOUND));
	}

	public PostDetailResponse getPost(Long id, Member actor) {
		Post post = getPostById(id);
		boardValidationService.validateWriter(post, actor);
		return PostDetailResponse.from(post);
	}

	@Transactional
	public PostDto createPost(PostRequest request, Member actor) {
		Post post = Post.of(request, actor);
		return PostDto.from(postRepository.save(post));
	}

	@Transactional
	public PostDto modifyPost(Long id, PostRequest request, Member actor) {
		Post post = getPostById(id);
		boardValidationService.validateWriter(post, actor);
		post.update(request);
		return PostDto.from(post);
	}

	@Transactional
	public void softDeletePost(Long id, Member actor) {
		Post post = getPostById(id);
		boardValidationService.validateWriter(post, actor);
		post.delete();
	}

	@Transactional
	public void clearAttachmentsByPostId(Long postId) {
		Post post = getPostById(postId);
		post.getAttachments().clear();
	}

	@Transactional
	public List<String> getAttachmentUrlsByPostId(Long postId) {
		return attachmentRepository.findUrlsByPostId(postId);
	}
}
