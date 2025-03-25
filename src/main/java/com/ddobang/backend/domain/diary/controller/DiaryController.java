package com.ddobang.backend.domain.diary.controller;

import com.ddobang.backend.domain.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class DiaryController {
    private final DiaryService diaryService;
}
