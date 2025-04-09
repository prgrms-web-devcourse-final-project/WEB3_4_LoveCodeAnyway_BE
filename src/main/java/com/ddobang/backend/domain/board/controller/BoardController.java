package com.ddobang.backend.domain.board.controller;

import com.ddobang.backend.domain.AuthHelper;
import com.ddobang.backend.domain.board.dto.PostDto;
import com.ddobang.backend.domain.board.dto.request.PostRequest;
import com.ddobang.backend.domain.board.dto.response.PostDetailResponse;
import com.ddobang.backend.domain.board.dto.response.PostSummaryResponse;
import com.ddobang.backend.domain.board.service.BoardService;
import com.ddobang.backend.domain.board.types.PostType;
import com.ddobang.backend.global.response.PageDto;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {
	private final BoardService boardService;
	private final AuthHelper authHelper;

	@GetMapping
	@Operation(summary = "나의 문의 보기")
	public ResponseEntity<SuccessResponse<PageDto<PostSummaryResponse>>> getMyPosts(
			@RequestParam(required = false) PostType type,
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		return ResponseFactory.ok(boardService.getMyPosts(type, keyword, page, size, authHelper.getCurrentMember().getId()));
	}

	@GetMapping("/{id}")
	@Operation(summary = "문의 상세 조회")
	public ResponseEntity<SuccessResponse<PostDetailResponse>> getPost(@PathVariable Long id) {
		return ResponseFactory.ok(boardService.getPost(id, authHelper.getCurrentMember()));
	}

	@PostMapping
	@Operation(summary = "문의 등록")
	public ResponseEntity<SuccessResponse<PostDto>> createPost(@RequestBody @Valid PostRequest request) {
		return ResponseFactory.created(boardService.createPost(request, authHelper.getCurrentMember()));
	}

	@PutMapping("/{id}")
	@Operation(summary = "문의 수정")
	public ResponseEntity<SuccessResponse<PostDto>> modifyPost(@PathVariable Long id,
																 @RequestBody @Valid PostRequest request) {
		return ResponseFactory.ok(boardService.modifyPost(id, request, authHelper.getCurrentMember()));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "문의 삭제")
	public ResponseEntity<Void> deletePost(@PathVariable Long id) {
		boardService.softDeletePost(id, authHelper.getCurrentMember());
		return ResponseFactory.noContent();
	}
}
