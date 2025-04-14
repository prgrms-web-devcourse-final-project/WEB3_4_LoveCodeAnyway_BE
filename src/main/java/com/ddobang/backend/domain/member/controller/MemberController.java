package com.ddobang.backend.domain.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.member.dto.request.UpdateProfileRequest;
import com.ddobang.backend.domain.member.dto.request.UpdateTagsRequest;
import com.ddobang.backend.domain.member.dto.response.BasicProfileResponse;
import com.ddobang.backend.domain.member.dto.response.MemberStatResponse;
import com.ddobang.backend.domain.member.dto.response.MemberTagResponse;
import com.ddobang.backend.domain.member.dto.response.OtherMemberProfileResponse;
import com.ddobang.backend.domain.member.dto.response.OtherProfileResponse;
import com.ddobang.backend.domain.member.dto.stat.EscapeProfileSummaryDto;
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
		summary = "닉네임 중복 확인 API",
		description = "닉네임의 중복 여부를 확인합니다, 사용 가능한 경우 true, 사용 중인 경우 false를 응답합니다."
	)
	@GetMapping("/check-nickname")
	public ResponseEntity<SuccessResponse<Boolean>> checkNicknameDuplicate(
		@RequestParam String nickname
	) {
		boolean isAvailable = !memberService.existsByNickname(nickname);
		String message = isAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
		return ResponseFactory.ok(message, isAvailable);
	}

	@Operation(
		summary = "내 프로필 조회 API",
		description = "내 프로필 정보를 조회하며, 닉네임, 성별, 소개글, 프로필 이미지, 매너 점수를 응답합니다."
	)
	@GetMapping("/me")
	public ResponseEntity<SuccessResponse<BasicProfileResponse>> getMyBasicProfile() {
		Member currentMember = loginMemberProvider.getCurrentMember();
		BasicProfileResponse response = BasicProfileResponse.of(currentMember);
		return ResponseEntity.ok(SuccessResponse.of("내 프로필 조회 성공", response));
	}

	@Operation(
		summary = "내 프로필 수정 API",
		description = "내 프로필 정보를 수정합니다. 닉네임, 소개글, 프로필 이미지를 수정할 수 있습니다."
	)
	@PatchMapping("/me")
	public ResponseEntity<SuccessResponse<BasicProfileResponse>> updateMyProfile(
		@RequestBody @Valid UpdateProfileRequest request
	) {
		Member currentMember = loginMemberProvider.getCurrentMember();
		BasicProfileResponse response = memberService.updateProfile(currentMember.getId(), request);
		return ResponseEntity.ok(SuccessResponse.of("내 프로필 수정 성공", response));
	}

	@Operation(
		summary = "내 사용자 태그 조회 API",
		description = "내 프로필에 설정된 사용자 태그를 조회하며, 태그 ID와 태그 이름을 응답합니다."
	)
	@GetMapping("/me/tags")
	public ResponseEntity<SuccessResponse<List<MemberTagResponse>>> getMyTags() {
		List<MemberTagResponse> tags = memberService.getMyTags();
		return ResponseEntity.ok(SuccessResponse.of("내 사용자태그 조회 성공", tags));
	}

	@Operation(
		summary = "내 사용자 태그 수정 API",
		description = "내 프로필에 설정된 사용자 태그를 수정합니다. 태그 ID 리스트를 요청 본문에 포함하여 수정합니다."
	)
	@PatchMapping("/me/tags")
	public ResponseEntity<SuccessResponse<Void>> updateMyTags(
		@RequestBody @Valid UpdateTagsRequest request
	) {
		Member currentMember = loginMemberProvider.getCurrentMember();
		memberService.updateTags(currentMember.getId(), request.tagIds());
		return ResponseEntity.ok(SuccessResponse.of("내 사용자태그 수정 성공"));
	}

	@Operation(
		summary = "내 요약 통계 조회 API",
		description = "프로필에서 사용하는 통계 정보(총 탈출 수, 성공률, 노힌트 성공률)를 응답합니다."
	)
	@GetMapping("/me/stats")
	public ResponseEntity<SuccessResponse<EscapeProfileSummaryDto>> getMyEscapeSummary() {
		Member currentMember = loginMemberProvider.getCurrentMember();
		EscapeProfileSummaryDto response = memberService.getStatsByMemberId(currentMember.getId());
		return ResponseEntity.ok(SuccessResponse.of("요약 통계 조회 성공", response));
	}

	@Operation(
		summary = "타인 공개 프로필 통합 조회 API",
		description = "타인 공개 프로필을 조회합니다. 닉네임, 성별, 소개글, 프로필 이미지, 매너 점수, 사용자 태그, 통계 정보를 응답합니다."
	)
	@GetMapping("/{memberId}/profile")
	public ResponseEntity<SuccessResponse<OtherMemberProfileResponse>> getOtherProfile(
		@PathVariable Long memberId
	) {
		OtherProfileResponse profile = memberService.getOtherProfile(memberId);
		List<MemberTagResponse> tags = memberService.getTagsByMemberId(memberId);
		EscapeProfileSummaryDto stats = memberService.getStatsByMemberId(memberId);

		OtherMemberProfileResponse response = OtherMemberProfileResponse.of(profile, tags, stats);
		return ResponseEntity.ok(SuccessResponse.of("타인 프로필 조회 성공", response));
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

		if (memberStatResponse == null) {
			return ResponseFactory.ok(
				"데이터가 없습니다. 탈출일지를 작성해주세요.",
				null
			);
		}

		return ResponseFactory.ok(
			"사용자 분석 데이터 조회 성공",
			memberStatResponse
		);
	}
}
