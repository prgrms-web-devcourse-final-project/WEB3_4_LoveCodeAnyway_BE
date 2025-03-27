package com.ddobang.backend.domain.message.controller;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.message.dto.MessageDto;
import com.ddobang.backend.domain.message.service.MessageService;
import com.ddobang.backend.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;
    private final MemberService memberService

    @GetMapping
    public ResponseEntity<SuccessResponse<MessageDto>> getAllMessages(
        @AuthenticationPrincipal Member member
    )
}
