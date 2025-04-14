package com.ddobang.backend.domain.theme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.entity.Theme;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long>, ThemeRepositoryCustom {

	@Query("""
		SELECT new com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse(t.id,t.name,s.name)
		FROM Theme t JOIN t.store s
		WHERE t.status != 'DELETED' AND (
			LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
			LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
		order by t.name asc
		""")
	List<SimpleThemeResponse> findThemesForDiarySearch(@Param("keyword") String keyword);
}
