package com.ddobang.backend.domain.diary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.diary.dto.request.DiaryFilterRequest;
import com.ddobang.backend.domain.diary.entity.Diary;

public interface DiaryRepositoryCustom {
	Page<Diary> findDiariesByFilter(DiaryFilterRequest request, Pageable pageable);
}
