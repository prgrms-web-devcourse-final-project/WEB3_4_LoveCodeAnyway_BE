package com.ddobang.backend.domain.diary.repository;

import java.util.List;

import com.querydsl.core.Tuple;

public interface DiaryStatRepositoryCustom {
	List<Tuple> top5TagCountSuccessCountByMember(Long authorId);

	Long countTotalGenreBaseByMember(Long authorId);
}
