package com.ddobang.backend.domain.message.controller;

/*
@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
	private final MessageService messageService;
	private final MemberService memberService;
	
	// 쪽지 전송
	@PostMapping
	public ResponseEntity<SuccessResponse<MessageDto>> sendMessage(
		@Valid @RequestBody MessageRequestDto requestDto,
		@AuthenticationPrincipal UserDetails userDetails) {

		Member sender = memberService.getMemberByUsername(userDetails.getUsername());
		Member receiver = memberService.getMemberById(requestDto.getReceiverId());

		MessageDto messageDto = messageService.sendMessage(
			sender,
			receiver,
			requestDto.getContent()
		);

		return ResponseFactory.ok("쪽지 전송 성공", messageDto);
	}

	// 페이징 처리: 받은 쪽지 목록 조회
	@GetMapping("/received")
	public ResponseEntity<SuccessResponse<Page<MessageDto>>> getReceivedMessages(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal UserDetails userDetails) {

		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		Page<MessageDto> messages = messageService.getReceivedMessagesWithPaging(member, page, size);

		return ResponseFactory.ok("받은 쪽지 목록 조회 성공", messages);
	}

	// 페이징 처리: 보낸 쪽지 목록 조회
	@GetMapping("/sent")
	public ResponseEntity<SuccessResponse<Page<MessageDto>>> getSentMessages(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal UserDetails userDetails) {

		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		Page<MessageDto> messages = messageService.getSentMessagesWithPaging(member, page, size);

		return ResponseFactory.ok("보낸 쪽지 목록 조회 성공", messages);
	}

	// 단일 쪽지 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<MessageDto>> getMessage(
		@PathVariable Long id,
		@AuthenticationPrincipal UserDetails userDetails) {

		Member member = memberService.getMemberByUsername(userDetails.getUsername());
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
		@PathVariable Long id,
		@AuthenticationPrincipal UserDetails userDetails) {

		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		MessageDto messageDto = messageService.updateIsRead(id, member);

		return ResponseFactory.ok("쪽지 읽음 상태 변경 성공", messageDto);
	}

	// 메시지 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> deleteMessage(
		@PathVariable Long id,
		@AuthenticationPrincipal UserDetails userDetails) {

		Member member = memberService.getMemberByUsername(userDetails.getUsername());
		messageService.deleteMessage(id, member);

		return ResponseFactory.ok("쪽지 삭제 성공", null);
	}
}*/
