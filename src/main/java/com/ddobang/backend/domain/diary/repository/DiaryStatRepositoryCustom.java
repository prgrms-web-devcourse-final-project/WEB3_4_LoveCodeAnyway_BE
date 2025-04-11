package com.ddobang.backend.domain.diary.repository;

import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;

public interface DiaryStatRepositoryCustom {
	List<Tuple> top5TagCountSuccessCountByMember(long authorId);

	Long countTotalGenreBaseByMember(long authorId);

	Map<Integer, Tuple> difficultyStatsWithHints(long authorId);

	Map<Integer, Tuple> difficultyStatsWithSatisfaction(long authorId);
}
