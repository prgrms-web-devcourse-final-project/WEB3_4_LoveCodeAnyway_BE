package com.ddobang.backend.domain.theme.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.theme.dto.response.ThemeTagResponse;
import com.ddobang.backend.domain.theme.entity.ThemeTag;

/**
 * ThemeTagRepository
 * @author 100minha
 */
@Repository
public interface ThemeTagRepository extends JpaRepository<ThemeTag, Long> {

	Optional<ThemeTag> findByName(String name);

	List<ThemeTag> findAllByIdIn(Collection<Long> ids);

	@Query("""
		SELECT new com.ddobang.backend.domain.theme.dto.response.ThemeTagResponse(t.id,t.name)
		FROM ThemeTag t
		""")
	List<ThemeTagResponse> findAllTags();
}
