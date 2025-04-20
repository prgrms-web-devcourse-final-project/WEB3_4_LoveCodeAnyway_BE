package com.ddobang.backend.domain.theme.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.repository.StoreRepository;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.global.config.QuerydslConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * ThemeStatRepositoryTest
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@Transactional
public class ThemeStatRepositoryTest {

	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private ThemeRepository themeRepository;
	@Autowired
	private ThemeStatRepository themeStatRepository;

	@PersistenceContext
	private EntityManager em;

	private Store store;
	private Theme theme;
	private ThemeStat themeStat;

	@BeforeEach
	void setUp() {
		em.createNativeQuery("ALTER TABLE theme ALTER COLUMN id RESTART WITH 1").executeUpdate();
		em.createNativeQuery("ALTER TABLE theme_stat ALTER COLUMN theme_id RESTART WITH 1").executeUpdate();

		store = storeRepository.save(Store.builder()
			.name("매장1")
			.status(Store.Status.OPENED)
			.build());
		theme = themeRepository.save(Theme.builder()
			.name("테마 ")
			.description("테마 설명 ")
			.officialDifficulty(3.0f)
			.runtime(60)
			.minParticipants(2)
			.maxParticipants(5)
			.price(25000)
			.status(Theme.Status.OPENED)
			.reservationUrl("https://example.com/theme/")
			.thumbnailUrl("https://placehold.co/600x400?text=Theme")
			.themeTags(Collections.emptyList())
			.store(store)
			.build());

		themeStat = themeStatRepository.save(ThemeStat.builder()
			.theme(theme)
			.difficulty(3)
			.fear(2)
			.activity(4)
			.satisfaction(5)
			.production(3)
			.story(4)
			.question(3)
			.interior(4)
			.deviceRatio(75)
			.noHintEscapeRate(80)
			.escapeResult(60)
			.escapeTimeAvg(3600)
			.build());
	}

	@Test
	@DisplayName("테마Id로 통계 조회")
	void findByIdTest() {
		// given
		Long themeId = 1L;

		// when
		ThemeStat foundThemeStat = themeStatRepository.findById(themeId).orElse(null);

		// then
		assertThat(foundThemeStat).isEqualTo(themeStat);
	}

	@Test
	@DisplayName("존재하지 않는 테마Id로 통계 조회")
	void findByIdNotFoundTest() {
		// given
		Long themeId = 999L;

		// when
		Optional<ThemeStat> foundThemeStat = themeStatRepository.findById(themeId);

		// then
		assertThat(foundThemeStat.isPresent()).isFalse();
	}

}
