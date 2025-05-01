package com.ddobang.backend.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.member.dto.response.MemberReviewResponse;
import com.ddobang.backend.domain.member.service.MemberReviewService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberReviewController {

	private final MemberReviewService memberReviewService;

	@GetMapping("/{id}/review")
	public ResponseEntity<SuccessResponse<MemberReviewResponse>> getMemberReview(
		@PathVariable Long id) {
		return ResponseFactory.ok(memberReviewService.getMemberReview(id));
	}
}
