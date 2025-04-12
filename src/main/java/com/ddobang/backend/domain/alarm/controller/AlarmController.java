package com.ddobang.backend.domain.alarm.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest;
import com.ddobang.backend.domain.alarm.dto.response.AlarmCountResponse;
import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.global.response.PageDto;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "알림", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {
	private final AlarmService alarmService;
	private final AlarmEventService alarmEventService;
	private final MemberService memberService;

	@Operation(summary = "알림 SSE 구독", description = "실시간 알림을 위한 SSE 연결을 구독합니다.")
	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribeAlarm(@AuthenticationPrincipal UserDetails userDetails) {
		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		return alarmEventService.subscribe(member.getId());
	}

	@Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 페이징하여 조회합니다.")
	@GetMapping
	public ResponseEntity<SuccessResponse<PageDto<AlarmResponse>>> getAlarms(
		@PageableDefault(size = 10) Pageable pageable,
		@AuthenticationPrincipal UserDetails userDetails) {
		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		PageDto<AlarmResponse> alarms = alarmService.getAlarms(member.getId(), pageable);
		return ResponseFactory.ok("알림 목록 조회 성공", alarms);
	}

	@Operation(summary = "알림 상세 조회", description = "특정 알림의 상세 정보를 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<AlarmResponse>> getAlarm(
		@PathVariable("id") Long alarmId,
		@AuthenticationPrincipal UserDetails userDetails) {
		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		AlarmResponse alarm = alarmService.getAlarm(alarmId, member.getId());
		return ResponseFactory.ok("알림 상세 조회 성공", alarm);
	}

	@Operation(summary = "알림 개수 조회", description = "사용자의 전체 및 읽지 않은 알림 개수를 조회합니다.")
	@GetMapping("/count")
	public ResponseEntity<SuccessResponse<AlarmCountResponse>> getAlarmCounts(
		@AuthenticationPrincipal UserDetails userDetails) {
		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		AlarmCountResponse counts = alarmService.getAlarmCounts(member.getId());
		return ResponseFactory.ok("알림 개수 조회 성공", counts);
	}

	@Operation(summary = "알림 생성", description = "새로운 알림을 생성합니다. (관리자시스템용)")
	@PostMapping
	public ResponseEntity<SuccessResponse<AlarmResponse>> createAlarm(
		@Valid @RequestBody AlarmCreateRequest request) {
		AlarmResponse createdAlarm = alarmService.createAlarm(request);
		return ResponseFactory.created("알림 생성 성공", createdAlarm);
	}

	@Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
	@PatchMapping("/{id}/read")
	public ResponseEntity<SuccessResponse<AlarmResponse>> markAsRead(
		@PathVariable("id") Long alarmId,
		@AuthenticationPrincipal UserDetails userDetails) {
		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		AlarmResponse updatedAlarm = alarmService.markAsRead(alarmId, member.getId());
		return ResponseFactory.ok("알림 읽음 처리 성공", updatedAlarm);
	}

	@Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
	@PatchMapping("/read-all")
	public ResponseEntity<SuccessResponse<Integer>> markAllAsRead(
		@AuthenticationPrincipal UserDetails userDetails) {
		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		int updatedCount = alarmService.markAllAsRead(member.getId());
		return ResponseFactory.ok("모든 알림 읽음 처리 성공", updatedCount);
	}

	@Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAlarm(
		@PathVariable("id") Long alarmId,
		@AuthenticationPrincipal UserDetails userDetails) {
		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		alarmService.deleteAlarm(alarmId, member.getId());
		return ResponseFactory.noContent();
	}

	// redirectUrl
	@Operation(summary = "알림 클릭 처리", description = "알림을 클릭했을 때 관련 페이지로 리다이렉트합니다.")
	@GetMapping("/{id}/redirect")
	public ResponseEntity<SuccessResponse<String>> redirectAlarm(
		@PathVariable("id") Long alarmId,
		@AuthenticationPrincipal UserDetails userDetails) {
		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		String redirectUrl = alarmService.getRedirectUrl(alarmId, member.getId());
		return ResponseFactory.ok("알림 리다이렉트 URL 조회 성공", redirectUrl);
	}
}