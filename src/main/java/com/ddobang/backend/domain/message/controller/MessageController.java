package com.ddobang.backend.domain.message.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.message.dto.MessageDto;
import com.ddobang.backend.domain.message.dto.MessageRequestDto;
import com.ddobang.backend.domain.message.service.MessageService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SliceDto;
import com.ddobang.backend.global.response.SuccessResponse;
import com.ddobang.backend.global.security.LoginMemberProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
	private final MessageService messageService;
	private final MemberService memberService;
	private final LoginMemberProvider loginMemberProvider; // 추가

	// 쪽지 전송
	@PostMapping
	public ResponseEntity<SuccessResponse<MessageDto>> sendMessage(
		@Valid @RequestBody MessageRequestDto requestDto,
		@AuthenticationPrincipal UserDetails userDetails) {

		// 변경: AuthenticationPrincipal 대신 LoginMemberProvider 사용
		Member sender = loginMemberProvider.getCurrentMember();
		Member receiver = memberService.getMemberById(requestDto.getReceiverId());

		MessageDto messageDto = messageService.sendMessage(
			sender,
			receiver,
			requestDto.getContent()
		);

		return ResponseFactory.ok("쪽지 전송 성공", messageDto);
	}

	// MessageController.java에서 커서 기반 엔드포인트 수정
	@GetMapping("/received")
	public ResponseEntity<SuccessResponse<SliceDto<MessageDto>>> getReceivedMessages(
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
		@RequestParam(defaultValue = "10") int size) {

		// 변경: LoginMemberProvider 사용
		Member member = loginMemberProvider.getCurrentMember();
		SliceDto<MessageDto> messages = messageService.getReceivedMessagesWithCursor(member, cursor, size);

		return ResponseFactory.ok("받은 쪽지 목록 조회 성공", messages);
	}

	@GetMapping("/sent")
	public ResponseEntity<SuccessResponse<SliceDto<MessageDto>>> getSentMessages(
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
		@RequestParam(defaultValue = "10") int size) {
		// 변경: LoginMemberProvider 사용
		Member member = loginMemberProvider.getCurrentMember();
		SliceDto<MessageDto> messages = messageService.getSentMessagesWithCursor(member, cursor, size);

		return ResponseFactory.ok("보낸 쪽지 목록 조회 성공", messages);
	}

	// 단일 쪽지 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<MessageDto>> getMessage(
		@PathVariable Long id) {

		// 변경: LoginMemberProvider 사용
		Member member = loginMemberProvider.getCurrentMember();
		MessageDto messageDto = messageService.getMessage(id, member);

		// 수신자와 로그인한 사용자가 같은 경우에만 읽음 상태 변경
		if (messageDto.getReceiverId().equals(member.getId()) && !messageDto.isRead()) {
			messageDto = messageService.updateIsRead(id, member);
		}

		return ResponseFactory.ok("쪽지 상세 조회 성공", messageDto);
	}

	// 쪽지 읽음 상태 변경
	@PatchMapping("/{id}/read")
	public ResponseEntity<SuccessResponse<MessageDto>> updateReadStatus(
		@PathVariable Long id) {

		// 변경: LoginMemberProvider 사용
		Member member = loginMemberProvider.getCurrentMember();
		MessageDto messageDto = messageService.updateIsRead(id, member);

		return ResponseFactory.ok("쪽지 읽음 상태 변경 성공", messageDto);
	}

	// 메시지 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> deleteMessage(
		@PathVariable Long id) {
		// 변경: LoginMemberProvider 사용
		Member member = loginMemberProvider.getCurrentMember();
		messageService.deleteMessage(id, member);

		return ResponseFactory.ok("쪽지 삭제 성공", null);
	}
}
