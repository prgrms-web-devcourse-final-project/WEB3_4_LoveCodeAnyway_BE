package com.ddobang.backend.domain.theme.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.global.config.QuerydslConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * ThemeTagRepositoryTest
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@Transactional
public class ThemeTagRepositoryTest {

	@Autowired
	private ThemeTagRepository themeTagRepository;

	@PersistenceContext
	EntityManager em;

	private ThemeTag tag1 = new ThemeTag("태그1");
	private ThemeTag tag2 = new ThemeTag("태그2");

	@BeforeEach
	void setUp() {
		em.createNativeQuery("ALTER TABLE theme_tag ALTER COLUMN id RESTART WITH 1").executeUpdate();

		tag1 = themeTagRepository.save(tag1);
		tag2 = themeTagRepository.save(tag2);
	}

	@Test
	@DisplayName("태그 이름으로 조회 성공 테스트")
	void findByNameTest() {
		// given
		String name = "태그1";

		// when
		ThemeTag foundTag = themeTagRepository.findByName(name).orElse(null);

		// then
		assertThat(foundTag).isNotNull();
		assertThat(foundTag).isEqualTo(tag1);
	}

	@Test
	@DisplayName("태그 이름으로 조회 실패 테스트")
	void findByNameFailTest() {
		// given
		String name = "태그3";

		// when
		ThemeTag foundTag = themeTagRepository.findByName(name).orElse(null);

		// then
		assertThat(foundTag).isNull();
	}

}
