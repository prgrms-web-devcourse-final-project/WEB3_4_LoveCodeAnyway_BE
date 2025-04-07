package com.ddobang.backend.domain.party.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.party.PartyAuthHelper;
import com.ddobang.backend.domain.party.dto.PartyDto;
import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartyDetailResponse;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.service.PartyService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SliceDto;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parties")
@Tag(name = "Party Controller")
public class PartyController {
	private final PartyService partyService;
	private final PartyAuthHelper partyAuthHelper;

	@GetMapping
	@Operation(summary = "모임 목록 조회 (무한 스크롤)")
	public ResponseEntity<SuccessResponse<SliceDto<PartySummaryResponse>>> getParties(
		@RequestParam(required = false) Long lastId,
		@RequestParam(defaultValue = "10") int size,
		@ModelAttribute PartySearchCondition partySearchCondition
	) {
		return ResponseFactory.ok(partyService.getParties(lastId, size, partySearchCondition));
	}

	@GetMapping("/{id}")
	@Operation(summary = "모임 상세 조회")
	public ResponseEntity<SuccessResponse<PartyDetailResponse>> getParty(@PathVariable Long id) {
		return ResponseFactory.ok(partyService.getPartyDetailResponse(id, partyAuthHelper.getCurrentMember()));
	}

	@PostMapping
	@Operation(summary = "모임 등록")
	public ResponseEntity<SuccessResponse<PartyDto>> createParty(@RequestBody @Valid PartyRequest request) {
		return ResponseFactory.created(partyService.createParty(request, partyAuthHelper.getCurrentMember()));
	}

	@PutMapping("/{id}")
	@Operation(summary = "모임 수정")
	public ResponseEntity<SuccessResponse<PartyDto>> modifyParty(@PathVariable Long id,
		@RequestBody @Valid PartyRequest request) {
		return ResponseFactory.ok(partyService.modifyParty(id, request, partyAuthHelper.getCurrentMember()));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "소프트 딜리트 (모임 삭제)")
	public ResponseEntity<Void> softDeleteParty(@PathVariable Long id) {
		partyService.softDeleteParty(id, partyAuthHelper.getCurrentMember());
		return ResponseFactory.noContent();
	}

	@PostMapping("/{id}/apply")
	@Operation(summary = "모임 참가 신청")
	public ResponseEntity<Void> applyParty(@PathVariable Long id) {
		partyService.applyParty(id, partyAuthHelper.getCurrentMember());
		return ResponseFactory.noContent();
	}

	@DeleteMapping("/{id}/cancel")
	@Operation(summary = "모임 참가 신청 취소")
	public ResponseEntity<Void> cancelAppliedParty(@PathVariable Long id) {
		partyService.cancelAppliedParty(id, partyAuthHelper.getCurrentMember());
		return ResponseFactory.noContent();
	}

	@PostMapping("/{id}/accept/{memberId}")
	@Operation(summary = "모임 신청 승인")
	public ResponseEntity<Void> acceptPartyMember(@PathVariable Long id, @PathVariable Long memberId) {
		partyService.acceptPartyMember(id, memberId, partyAuthHelper.getCurrentMember());
		return ResponseFactory.noContent();
	}

	@PatchMapping("/{id}/executed")
	@Operation(summary = "모임 실행 완료")
	public ResponseEntity<Void> executeParty(@PathVariable Long id) {
		partyService.executeParty(id, partyAuthHelper.getCurrentMember());
		return ResponseFactory.noContent();
	}

	@PatchMapping("/{id}/unexcuted")
	@Operation(summary = "모임 미실행 완료")
	public ResponseEntity<Void> unexecuteParty(@PathVariable Long id) {
		partyService.unexecuteParty(id, partyAuthHelper.getCurrentMember());
		return ResponseFactory.noContent();
	}
}
