package com.ddobang.backend.domain.diary.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.diary.entity.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {
	List<Diary> findByAuthorIdAndDiaryStat_EscapeDateBetween(long authorId, LocalDate startDate, LocalDate endDate);

	Optional<Diary> findByAuthorIdAndThemeId(Long authorId, Long themeId);
}
