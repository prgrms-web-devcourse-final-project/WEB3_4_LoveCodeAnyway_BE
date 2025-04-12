package com.ddobang.backend.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.member.dto.request.ProfileRequest;
import com.ddobang.backend.domain.member.dto.response.BasicProfileResponse;
import com.ddobang.backend.domain.member.dto.response.MemberStatResponse;
import com.ddobang.backend.domain.member.dto.response.OtherProfileResponse;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;
import com.ddobang.backend.global.security.LoginMemberProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "MemberController", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;
	private final LoginMemberProvider loginMemberProvider;

	@Operation(
		summary = "타인 프로필 조회 API",
		description = "특정 사용자의 공개된 프로필 정보를 조회합니다."
			+ "닉네임, 성별, 소개글, 프로필 이미지, 매너 점수, 방장 횟수를 응답합니다."
	)
	@PostMapping("/profile")
	public ResponseEntity<SuccessResponse<OtherProfileResponse>> getOtherProfile(
		@RequestBody @Valid ProfileRequest request
	) {
		OtherProfileResponse profile = memberService.getOtherProfile(request.memberId());

		return ResponseFactory.ok(profile);
	}

	@Operation(
		summary = "내 기본 프로필 조회 API",
		description = "내 프로필 정보를 조회합니다."
			+ "닉네임, 성별, 소개글, 프로필 이미지, 매너 점수를 응답합니다."
	)
	@GetMapping("/me")
	public ResponseEntity<SuccessResponse<BasicProfileResponse>> getMyBasicProfile() {
		Member currentMember = loginMemberProvider.getCurrentMember();
		BasicProfileResponse response = BasicProfileResponse.of(currentMember);
		return ResponseEntity.ok(SuccessResponse.of("기본 프로필 조회 성공", response));
	}

	@Operation(
		summary = "사용자 분석 페이지 조회 API",
		description = "자신의 사용자 분석 정보를 조회합니다."
			+ "데이터가 없는 경우에는 null을 반환합니다."
	)
	@GetMapping("/stat")
	public ResponseEntity<SuccessResponse<MemberStatResponse>> getMemberStat() {
		Member currentMember = loginMemberProvider.getCurrentMember();
		MemberStatResponse memberStatResponse = memberService.getMemberStat(currentMember);

		return ResponseFactory.ok(
			memberStatResponse
		);
	}
}
