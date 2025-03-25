package com.ddobang.backend.domain.message.controller;

import com.ddobang.backend.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;
}
