package com.ddobang.backend.domain.theme.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.ArrayList;
import java.util.List;
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
import com.ddobang.backend.domain.theme.dto.request.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.global.config.JpaAuditingConfig;
import com.ddobang.backend.global.config.QuerydslConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * ThemeRepositoryTest
 * 테마에 관련된 모든 repository에 대한 테스트 코드 입니다.
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(value = {QuerydslConfig.class, JpaAuditingConfig.class})
@Transactional
public class ThemeRepositoryTest {
	@Autowired
	private ThemeRepository themeRepository;
	@Autowired
	private ThemeTagRepository themeTagRepository;
	@Autowired
	private ThemeStatRepository themeStatRepository;
	@Autowired
	private StoreRepository storeRepository;

	@PersistenceContext
	private EntityManager em;

	private Store store = Store.builder()
		.name("매장1")
		.status(Store.Status.OPENED)
		.build();

	private ThemeTag tag1 = new ThemeTag("태그1");
	private ThemeTag tag2 = new ThemeTag("태그2");

	private List<Theme> testThemes = new ArrayList<>();
	private List<ThemeStat> themeStats = new ArrayList<>();

	@BeforeEach
	void setUp() {
		em.createNativeQuery("ALTER TABLE theme ALTER COLUMN id RESTART WITH 1").executeUpdate();

		tag1 = themeTagRepository.save(tag1);
		tag2 = themeTagRepository.save(tag2);
		store = storeRepository.save(store);

		for (int i = 1; i <= 5; i++) {
			testThemes.add(themeRepository.save(Theme.builder()
				.name("방탈출" + i)
				.description("방탈출설명" + i)
				.officialDifficulty(4.0f)
				.minParticipants(1)
				.maxParticipants(8)
				.status(Theme.Status.OPENED)
				.store(store)
				.themeTags(List.of(tag1, tag2))
				.thumbnailUrl("test.thumbnail")
				.build()));
		}

		for (int j = 1; j <= 5; j++) {
			themeStats.add(themeStatRepository.save(ThemeStat.builder()
				.theme(testThemes.get(j - 1))
				.difficulty(3)
				.fear(2)
				.activity(4)
				.satisfaction(j)
				.production(3)
				.story(4)
				.question(3)
				.interior(4)
				.deviceRatio(75)
				.noHintEscapeRate(80)
				.escapeResult(60)
				.escapeTimeAvg(3600)
				.diaryCount(5 / j)
				.build()));
		}
	}

	@Test
	@DisplayName("id로 테마 조회 성공 테스트")
	void findByIdTest() {
		// given
		Long id = 1L;

		// when
		Optional<Theme> oTheme = themeRepository.findById(id);

		// then
		assertThat(oTheme.isPresent()).isTrue();
		Theme theme = oTheme.get();
		assertThat(theme).isEqualTo(testThemes.getFirst());
	}

	@Test
	@DisplayName("id로 테마 조회 실패 테스트")
	void findByIdFailTest() {
		// given
		Long id = 100L;

		// when
		Optional<Theme> oTheme = themeRepository.findById(id);

		// then
		assertThat(oTheme.isPresent()).isFalse();
	}

	@Test
	@DisplayName("필터 없이 전체 테마 조회")
	void findAllThemesWithoutFilter() {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, null, null);

		// when
		List<Theme> results = themeRepository.findThemesByFilter(request, 0, 10);

		// then
		assertThat(results).hasSize(5);
	}

	@Test
	@DisplayName("태그 이름으로 필터링된 테마 조회")
	void findThemesByTagName() {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, List.of(tag1.getId()), null, null);

		// when
		List<Theme> results = themeRepository.findThemesByFilter(request, 0, 10);

		// then
		assertThat(results).hasSize(5);
	}

	@Test
	@DisplayName("참가 인원으로 필터링된 테마 조회")
	void findThemesByParticipants() {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, 1, null);

		// when
		List<Theme> results = themeRepository.findThemesByFilter(request, 0, 10);

		// then
		assertThat(results).hasSize(5);
	}

	@Test
	@DisplayName("키워드로 테마 이름 검색")
	void findThemesByKeyword() {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, null, "방탈출1");

		// when
		List<Theme> results = themeRepository.findThemesByFilter(request, 0, 10);

		// then
		assertThat(results).hasSize(1);
		assertThat(results.get(0).getName()).isEqualTo("방탈출1");
	}

	@Test
	@DisplayName("복합 조건 필터링 - 태그, 인원수, 키워드")
	void findThemesWithMultipleFilters() {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, List.of(1L), 1, "방탈출");

		// when
		List<Theme> results = themeRepository.findThemesByFilter(request, 0, 10);

		// then
		assertThat(results).hasSize(5);
	}

	@Test
	@DisplayName("페이지네이션 테스트 - 첫 페이지")
	void findThemesWithPaginationPage0() {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, null, null);

		// when
		List<Theme> results = themeRepository.findThemesByFilter(request, 0, 3);

		// then
		assertThat(results).hasSize(4);
		assertThat(results.get(0)).isIn(testThemes);
		assertThat(results.get(1)).isIn(testThemes);
		assertThat(results.get(2)).isIn(testThemes);
	}

	@Test
	@DisplayName("페이지네이션 테스트 - 두 번째 페이지")
	void findThemesWithPaginationPage1() {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, null, null);

		// when
		List<Theme> results = themeRepository.findThemesByFilter(request, 1, 3);

		// then
		assertThat(results).hasSize(2);
		assertThat(results.get(0)).isIn(testThemes);
		assertThat(results.get(1)).isIn(testThemes);
	}

	@Test
	@DisplayName("테마 이름으로 모임 등록 전용 검색 테스트")
	void findThemesForPartySearchByThemeTest() {
		// given
		String keyword = "탈출3";

		// when
		List<Theme> results =
			themeRepository.findThemesForPartySearch(keyword);

		// then
		assertThat(results).hasSize(1);
		assertThat(results.getFirst()).isEqualTo(testThemes.get(2));
	}

	@Test
	@DisplayName("매장 이름으로 모임 등록 전용 검색 테스트")
	void findThemesForPartySearchNoResultTest() {
		// given
		String keyword = "매장";

		// when
		List<Theme> results =
			themeRepository.findThemesForPartySearch(keyword);

		// then
		assertThat(results).hasSize(5);
		assertThat(results.getLast()).isEqualTo(testThemes.getLast());
	}

	@Test
	@DisplayName("존재하지 않는 키워드로 모임 등록 전용 검색 테스트")
	void findThemesForPartySearchByStoreTest() {
		// given
		String keyword = "NO_CONTENT";

		// when
		List<Theme> results =
			themeRepository.findThemesForPartySearch(keyword);

		// then
		assertThat(results).hasSize(0);
	}

	@Test
	@DisplayName("테마 이름으로 일지 작성 전용 검색 테스트")
	void findThemesForDiarySearchByThemeTest() {
		// given
		String keyword = "탈출2";

		// when
		List<SimpleThemeResponse> results =
			themeRepository.findThemesForDiarySearch(keyword);

		// then
		assertThat(results).hasSize(1);
		assertThat(results.get(0).themeName()).isEqualTo(testThemes.get(1).getName());
		assertThat(results.get(0).storeName()).isEqualTo(store.getName());
	}

	@Test
	@DisplayName("매장 이름으로 일지 작성 전용 검색 테스트")
	void findThemesForDiarySearchByStoreTest() {
		// given
		String keyword = "매장";

		// when
		List<SimpleThemeResponse> results =
			themeRepository.findThemesForDiarySearch(keyword);

		// then
		assertThat(results).hasSize(5);
		assertThat(results.get(2).themeName()).isEqualTo(testThemes.get(2).getName());
		assertThat(results.get(2).storeName()).isEqualTo(store.getName());
	}

	@Test
	@DisplayName("존재하지 않는 키워드로 일지 작성 전용 검색 테스트")
	void findThemesForDiarySearchNoResultTest() {
		// given
		String keyword = "NO_CONTENT";

		// when
		List<SimpleThemeResponse> results =
			themeRepository.findThemesForDiarySearch(keyword);

		// then
		assertThat(results).hasSize(0);
	}

	@Test
	@DisplayName("인기 테마 조회 테스트")
	void findTop10PopularThemesByTagNameTest() {
		// given
		String tagName = tag1.getName();

		// when
		List<Theme> results = themeRepository.findTop10PopularThemesByTagName(tagName);

		// then
		assertThat(results).hasSize(5);
		assertThat(results.get(0)).isEqualTo(testThemes.get(0));
		assertThat(results.get(1)).isEqualTo(testThemes.get(4));
		assertThat(results.get(2)).isEqualTo(testThemes.get(1));
	}

	@Test
	@DisplayName("없는 태그로 인기 테마 조회 테스트")
	void findTop10PopularThemesByInvalidTagNameTest() {
		// given
		String tagName = "INVALID_TAG";

		// when
		List<Theme> results = themeRepository.findTop10PopularThemesByTagName(tagName);

		// then
		assertThat(results.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("최신 테마 조회 테스트")
	void findTop10NewestThemesByTagNameTest() {
		// given
		String tagName = tag1.getName();

		// when
		List<Theme> results = themeRepository.findTop10NewestThemesByTagName(tagName);

		// then
		assertThat(results).hasSize(5);
		assertThat(results.get(0)).isIn(testThemes);
		assertThat(results.get(2)).isIn(testThemes);
	}

	@Test
	@DisplayName("없는 태그로 최신 테마 조회 테스트")
	void findTop10NewestThemesByInvalidTagNameTest() {
		// given
		String tagName = "INVALID_TAG";

		// when
		List<Theme> results = themeRepository.findTop10NewestThemesByTagName(tagName);

		// then
		assertThat(results.isEmpty()).isTrue();
	}
}
