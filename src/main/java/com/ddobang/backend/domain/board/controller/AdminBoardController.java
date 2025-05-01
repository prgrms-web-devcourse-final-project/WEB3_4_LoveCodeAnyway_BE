package com.ddobang.backend.domain.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.board.dto.PostReplyDto;
import com.ddobang.backend.domain.board.dto.request.AdminPostSearchCondition;
import com.ddobang.backend.domain.board.dto.request.PostReplyRequest;
import com.ddobang.backend.domain.board.dto.response.AdminPostDetailResponse;
import com.ddobang.backend.domain.board.dto.response.PostSummaryResponse;
import com.ddobang.backend.domain.board.service.AdminBoardService;
import com.ddobang.backend.global.response.PageDto;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/boards")
public class AdminBoardController {
	private final AdminBoardService adminBoardService;

	@PostMapping("/search")
	@Operation(summary = "관리자 - 문의 목록 조회")
	public ResponseEntity<SuccessResponse<PageDto<PostSummaryResponse>>> getPosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestBody(required = false) AdminPostSearchCondition adminPostSearchCondition
	) {
		return ResponseFactory.ok(adminBoardService.getPostsForAdmin(page, size, adminPostSearchCondition));
	}

	@GetMapping("/{id}")
	@Operation(summary = "관리자 - 문의 상세 조회")
	public ResponseEntity<SuccessResponse<AdminPostDetailResponse>> getPostForAdmin(@PathVariable Long id) {
		return ResponseFactory.ok(adminBoardService.getPostForAdmin(id));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "관리자 - 문의글 삭제")
	public ResponseEntity<Void> deletePost(
		@PathVariable Long id) {
		adminBoardService.deletePost(id);
		return ResponseFactory.noContent();
	}

	@PostMapping("/{id}/reply")
	@Operation(summary = "관리자 - 답변 등록")
	public ResponseEntity<SuccessResponse<PostReplyDto>> createReply(
		@PathVariable Long id, @RequestBody @Valid PostReplyRequest request) {
		return ResponseFactory.created(adminBoardService.createReply(id, request));
	}

	@PutMapping("/{id}/reply/{replyId}")
	@Operation(summary = "관리자 - 답변 수정")
	public ResponseEntity<SuccessResponse<PostReplyDto>> modifyReply(
		@PathVariable Long id, @PathVariable Long replyId, @RequestBody @Valid PostReplyRequest request) {
		return ResponseFactory.ok(adminBoardService.modifyReply(id, replyId, request));
	}

	@DeleteMapping("/{id}/reply/{replyId}")
	@Operation(summary = "관리자 - 답변 삭제")
	public ResponseEntity<Void> deleteReply(
		@PathVariable Long id, @PathVariable Long replyId) {
		adminBoardService.deleteReply(id, replyId);
		return ResponseFactory.noContent();
	}
}
