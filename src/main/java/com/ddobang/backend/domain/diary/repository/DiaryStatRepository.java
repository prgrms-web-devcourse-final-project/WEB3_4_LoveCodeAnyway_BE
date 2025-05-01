package com.ddobang.backend.domain.diary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.diary.entity.DiaryStat;

@Repository
public interface DiaryStatRepository extends JpaRepository<DiaryStat, Long>, DiaryStatRepositoryCustom {
	List<DiaryStat> findByThemeId(Long themeId);

	List<DiaryStat> findByAuthorId(Long memberId);
}
