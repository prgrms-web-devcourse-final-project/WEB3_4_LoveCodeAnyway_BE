package com.ddobang.backend.domain.diary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.diary.dto.request.DiaryFilterRequest;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.member.entity.Member;

public interface DiaryRepositoryCustom {
	Page<Diary> findDiariesByFilter(Member author, DiaryFilterRequest request, Pageable pageable);
}
