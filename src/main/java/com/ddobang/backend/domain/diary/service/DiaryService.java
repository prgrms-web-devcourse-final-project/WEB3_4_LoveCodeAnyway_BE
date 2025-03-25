package com.ddobang.backend.domain.diary.service;

import com.ddobang.backend.domain.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
}
