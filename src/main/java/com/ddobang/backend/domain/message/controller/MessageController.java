package com.ddobang.backend.domain.message.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.message.dto.MessageDto;
import com.ddobang.backend.domain.message.dto.MessageRequestDto;
import com.ddobang.backend.domain.message.entity.Message;
import com.ddobang.backend.domain.message.exception.MessageErrorCode;
import com.ddobang.backend.domain.message.exception.MessageException;
import com.ddobang.backend.domain.message.service.MessageService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {
	private final MessageService messageService;
	private final MemberService memberService;

	// 쪽지 목록 조회
	@GetMapping
	public ResponseEntity<SuccessResponse<List<MessageDto>>> getAllMessages(
		@AuthenticationPrincipal UserDetails userDetails) {

		String username = userDetails.getUsername();
		Member member = memberService.getMemberByUsername(username);

		List<Message> messages = messageService.getAllMessages(member.getId());
		List<MessageDto> messageDtos = messages.stream()
			.map(MessageDto::fromEntity)
			.collect(Collectors.toList());

		return ResponseFactory.ok("쪽지 목록 조회 성공", messageDtos);
	}

	// 쪽지 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<MessageDto>> getMessage(
		@PathVariable Long id,
		@AuthenticationPrincipal UserDetails userDetails) {

		String username = userDetails.getUsername();
		Member member = memberService.getMemberByUsername(username);
		Message message = messageService.getMessage(id);

		// 쪽지 수신자와 로그인한 사용자가 같은 경우에만 읽음 상태 변경
		if (message.getReceiver().getId().equals(member.getId()) && !message.isRead()) {
			messageService.updateIsRead(id, true);
		}

		return ResponseFactory.ok("쪽지 상세 조회 성공", MessageDto.fromEntity(message));
	}

	// 쪽지 전송
	@PostMapping
	public ResponseEntity<SuccessResponse<MessageDto>> sendMessage(
		@Valid @RequestBody MessageRequestDto requestDto,
		@AuthenticationPrincipal UserDetails userDetails) {

		String username = userDetails.getUsername();
		Member sender = memberService.getMemberByUsername(username);
		Member receiver = memberService.getMemberById(requestDto.getReceiverId());

		Message message = messageService.sendMessage(
			sender,
			receiver,
			requestDto.getContent()
		);

		return ResponseFactory.ok("쪽지 전송 성공", MessageDto.fromEntity(message));
	}

	// 쪽지 읽음 상태 변경
	@PatchMapping("/{id}/read-status")
	public ResponseEntity<SuccessResponse<Void>> updateReadStatus(
		@PathVariable Long id,
		@AuthenticationPrincipal UserDetails userDetails) {

		String username = userDetails.getUsername();
		Member member = memberService.getMemberByUsername(username);
		Message message = messageService.getMessage(id);

		// 쪽지 수신자와 로그인한 사용자가 같은 경우에만 읽음 상태 변경
		if (message.getReceiver().getId().equals(member.getId())) {
			messageService.updateIsRead(id, true);
			return ResponseFactory.ok("쪽지 읽음 상태 변경 성공", null);
		} else {
			throw new MessageException(MessageErrorCode.MESSAGE_READ_FORBIDDEN);
		}
	}

	// 쪽지 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> deleteMessage(
		@PathVariable Long id,
		@AuthenticationPrincipal UserDetails userDetails) {

		String username = userDetails.getUsername();
		Member member = memberService.getMemberByUsername(username);
		Message message = messageService.getMessage(id);

		// 쪽지 수신자와 로그인한 사용자가 같은 경우에만 삭제
		if (message.getReceiver().getId().equals(member.getId())) {
			messageService.deleteMessage(id);
			return ResponseFactory.ok("쪽지 삭제 성공", null);
		} else {
			throw new MessageException(MessageErrorCode.MESSAGE_DELETE_FORBIDDEN);
		}
	}
}