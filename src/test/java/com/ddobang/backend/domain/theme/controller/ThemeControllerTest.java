package com.ddobang.backend.domain.theme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.dto.request.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.domain.theme.exception.ThemeErrorCode;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
import com.ddobang.backend.global.initdata.BaseInitData;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ThemeControllerTest
 * 테마 컨트롤러 통합 테스트
 * @author 100minha
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ThemeControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BaseInitData initThemeMockData;

	@Autowired
	private ThemeStatRepository themeStatRepository;

	private Region region1; // 서울 / 강남
	private Region region2; // 서울 / 홍대

	private Store store1; // 방탈출 A (서울 강남구, region1)
	private Store store2; // 방탈출 B (서울 마포구, region2)

	private ThemeTag tag1; // 공포
	private ThemeTag tag2; // 감성
	private ThemeTag tag3; // 판타지

	private List<Theme> themes;

	private ThemeStat themeStat;
	// themes 0~4번 인덱스 테마 (테마 1~5)에 매핑된 통계

	@BeforeEach
	void setup() {
		region1 = initThemeMockData.getRegion1();
		region2 = initThemeMockData.getRegion2();

		store1 = initThemeMockData.getStore1();
		store2 = initThemeMockData.getStore2();

		tag1 = initThemeMockData.getTag1();
		tag2 = initThemeMockData.getTag2();
		tag3 = initThemeMockData.getTag3();

		themes = initThemeMockData.getThemes();
		//themeStat = initThemeMockData.getThemeStats().getFirst();
		themeStat = themeStatRepository.findAll().getFirst();
	}

	private ResultActions performGetThemesWithFilter(
		int page, ThemeFilterRequest request) throws Exception {
		return mvc.perform(post("/api/v1/themes")
			.param("page", String.valueOf(page))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		);
	}

	@Test
	@DisplayName("필터 없이 테마 다건 조회 테스트")
	void getThemesWhenNoFilterTest() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(
			null, null, null, null);

		// when
		ResultActions result = performGetThemesWithFilter(0, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content[0].name").value(themes.get(28).getName()))
			.andExpect(jsonPath("$.data.content[0].tags[0]").value(tag1.getName()))
			.andExpect(jsonPath("$.data.content[0].tags[1]").value(tag2.getName()))
			.andExpect(jsonPath("$.data.content[1].name").value(themes.get(27).getName()))
			.andExpect(jsonPath("$.data.content[1].tags[0]").value(tag3.getName()))
			.andExpect(jsonPath("$.data.content[1].recommendedParticipants").value("2~3인"))
			.andExpect(jsonPath("$.data.content[2].name").value(themes.get(25).getName()))
		;
	}

	@Test
	@DisplayName("단수 지역 필터로 테마 다건 조회 테스트")
	void getThemesWithOneRegionFilterTest() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(
			List.of(1L), null, null, null);

		// when
		ResultActions result = performGetThemesWithFilter(0, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content[0].name").value(themes.get(27).getName()))
			.andExpect(jsonPath("$.data.content[0].tags[0]").value(tag3.getName()))
			.andExpect(jsonPath("$.data.content[1].name").value(themes.get(25).getName()))
			.andExpect(jsonPath("$.data.content[1].tags[0]").value(tag1.getName()))
			.andExpect(jsonPath("$.data.content[1].storeName").value(store1.getName()))
			.andExpect(jsonPath("$.data.content[2].name").value(themes.get(21).getName()))
		;
	}

	// 10, 8, 7, 5, 4번 테마
	@Test
	@DisplayName("복수 지역 필터로 테마 다건 조회 테스트")
	void getThemesWithManyRegionFilterTest() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(
			List.of(1L, 2L), null, null, null);

		// when
		ResultActions result = performGetThemesWithFilter(0, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content[0].name").value(themes.get(28).getName()))
			.andExpect(jsonPath("$.data.content[0].tags[0]").value(tag1.getName()))
			.andExpect(jsonPath("$.data.content[0].tags[1]").value(tag2.getName()))
			.andExpect(jsonPath("$.data.content[1].name").value(themes.get(27).getName()))
			.andExpect(jsonPath("$.data.content[1].tags[0]").value(tag3.getName()))
			.andExpect(jsonPath("$.data.content[1].recommendedParticipants").value("2~3인"))
			.andExpect(jsonPath("$.data.content[2].name").value(themes.get(25).getName()))
		;
	}

	@Test
	@DisplayName("공포 태그 필터로 테마 다건 조회 테스트")
	void getThemesWithTag1FilterTest() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, List.of(1L), null, null);

		// when
		ResultActions result = performGetThemesWithFilter(0, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content[0].name").value(themes.get(28).getName()))
			.andExpect(jsonPath("$.data.content[1].name").value(themes.get(25).getName()))
			.andExpect(jsonPath("$.data.content[2].name").value(themes.get(24).getName()));
	}

	@Test
	@DisplayName("2인 인원수 필터로 테마 다건 조회 테스트")
	void getThemesWithParticipantsFilterTest() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, 2, null);

		// when
		ResultActions result = performGetThemesWithFilter(0, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content[0].name").value(themes.get(27).getName()))
			.andExpect(jsonPath("$.data.content[1].name").value(themes.get(25).getName()));
	}

	@Test
	@DisplayName("키워드 필터로 테마 다건 조회 테스트")
	void getThemesWithKeywordFilterTest() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, null, "테마 1");

		// when
		ResultActions result = performGetThemesWithFilter(0, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content[0].name").value("테마 19"))
			.andExpect(jsonPath("$.data.content[1].name").value("테마 17"));
	}

	@Test
	@DisplayName("복합 필터(지역 + 태그 + 인원수 + 키워드)로 테마 다건 조회 테스트")
	void getThemesWithComplexFilterTest() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(
			List.of(region2.getId()), List.of(2L), 5, "테마 5");

		// when
		ResultActions result = performGetThemesWithFilter(0, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.length()").value(1))
			.andExpect(jsonPath("$.data.content[0].name").value(themes.get(4).getName()))
			.andExpect(jsonPath("$.data.content[0].tags.length()").value(2))
		;
	}

	@Test
	@DisplayName("복합 필터(지역 + 태그 + 인원수 + 키워드)로 테마 다건 조회(조회된 테마 없음) 테스트")
	void cannotFindThemesWithComplexFilterTest() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(
			List.of(region2.getId()), List.of(2L), 5, "A");

		// when
		ResultActions result = performGetThemesWithFilter(0, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.length()").value(0))
		;
	}

	@Test
	@DisplayName("페이지네이션 - 1페이지 조회 테스트")
	void getThemesWithPaginationPage1Test() throws Exception {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, null, null);

		// when
		ResultActions result = performGetThemesWithFilter(1, request);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.hasNext").value(true))
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content[0].name").value(themes.get(21).getName()))
			.andExpect(jsonPath("$.data.content[1].name").value(themes.get(19).getName()));
	}

	@Test
	@DisplayName("테마 상세(테마 통계 존재) 조회 테스트")
	void getThemeWithStatTest() throws Exception {
		// given
		long themeId = 1L;

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/" + themeId)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.name").value(themes.get(0).getName()))
			.andExpect(jsonPath("$.data.storeInfo.name").value(store2.getName()))
			.andExpect(jsonPath("$.data.runtime").value(themes.get(0).getRuntime()))
			.andExpect(jsonPath("$.data.recommendedParticipants").value("4~5인"))
			.andExpect(jsonPath("$.data.tags[0]").value(tag1.getName()))
			.andExpect(jsonPath("$.data.tags[1]").value(tag2.getName()))
			.andExpect(jsonPath("$.data.diaryBasedThemeStat").isNotEmpty())
			.andExpect(jsonPath("$.data.diaryBasedThemeStat.fear").value(themeStat.getFear()))
		;
	}

	@Test
	@DisplayName("테마 상세(테마 통계 없음) 조회 테스트")
	void getThemeWithNotStatTest() throws Exception {
		// given
		long themeId = 10L;

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/" + themeId)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.name").value(themes.get(9).getName()))
			.andExpect(jsonPath("$.data.storeInfo.name").value(store1.getName()))
			.andExpect(jsonPath("$.data.runtime").value(themes.get(9).getRuntime()))
			.andExpect(jsonPath("$.data.recommendedParticipants").value("2~3인"))
			.andExpect(jsonPath("$.data.tags[0]").value(tag1.getName()))
			.andExpect(jsonPath("$.data.tags[1]").value(tag2.getName()))
			.andExpect(jsonPath("$.data.diaryBasedThemeStat").isEmpty());
	}

	@Test
	@DisplayName("존재하지 않는 테마 id 조회 테스트")
	void getThemeWithNotExistTest() throws Exception {
		// given
		long themeId = 999L;

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/" + themeId)
			.contentType(MediaType.APPLICATION_JSON)
		);
		ThemeErrorCode errorCode = ThemeErrorCode.THEME_NOT_FOUND;

		// then
		result
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errorCode").value(errorCode.getErrorCode()))
			.andExpect(jsonPath("$.message").value(errorCode.getMessage()))
		;
	}

	@Test
	@DisplayName("테마 이름으로 모임 등록 전용 검색 테스트")
	void getThemesForPartySearchByThemeTest() throws Exception {
		// given
		String keyword = "테마 7";

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/search-for-party")
			.param("keyword", keyword)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(1))
			.andExpect(jsonPath("$.data[0].name").value(themes.get(6).getName()))
			.andExpect(jsonPath("$.data[0].tags[0]").value(tag1.getName()))
			.andExpect(jsonPath("$.data[0].tags[1]").value(tag2.getName()))
		;
	}

	@Test
	@DisplayName("매장 이름으로 모임 등록 전용 검색 테스트")
	void getThemesForPartySearchNoResultTest() throws Exception {
		// given
		String keyword = "탈출 A";

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/search-for-party")
			.param("keyword", keyword)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(10))
			.andExpect(jsonPath("$.data[1].name").value(themes.get(3).getName()))
			.andExpect(jsonPath("$.data[1].tags[0]").value(tag3.getName()))
		;
	}

	@Test
	@DisplayName("존재하지 않는 키워드로 모임 등록 전용 검색 테스트")
	void getThemesForPartySearchByStoreTest() throws Exception {
		// given
		String keyword = "NO_CONTENT";

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/search-for-party")
			.param("keyword", keyword)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(0))
		;
	}

	@Test
	@DisplayName("테마 이름으로 일지 작성 전용 검색 테스트")
	void getThemesForDiarySearchByThemeTest() throws Exception {
		// given
		String keyword = "테마 8";

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/search-for-diary")
			.param("keyword", keyword)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(1))
			.andExpect(jsonPath("$.data[0].themeId").value(themes.get(7).getId()))
			.andExpect(jsonPath("$.data[0].themeName").value(themes.get(7).getName()))
			.andExpect(jsonPath("$.data[0].storeName").value(store1.getName()))
		;
	}

	@Test
	@DisplayName("매장 이름으로 일지 작성 전용 검색 테스트")
	void getThemesForDiarySearchByStoreTest() throws Exception {
		// given
		String keyword = "탈출 B";

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/search-for-diary")
			.param("keyword", keyword)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(15))
			.andExpect(jsonPath("$.data[1].themeId").value(themes.get(10).getId()))
			.andExpect(jsonPath("$.data[1].themeName").value(themes.get(10).getName()))
			.andExpect(jsonPath("$.data[1].storeName").value(store2.getName()))
		;
	}

	@Test
	@DisplayName("존재하지 않는 키워드로 일지 작성 전용 검색 테스트")
	void getThemesForDiarySearchNoResultTest() throws Exception {
		// given
		String keyword = "NO_CONTENT";

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/search-for-diary")
			.param("keyword", keyword)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(0))
		;
	}

	@Test
	@DisplayName("테마 태그 목록 조회 테스트")
	void getAllThemeTagsTest() throws Exception {
		// given
		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/tags")
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(3))
			.andExpect(jsonPath("$.data[0].name").value(tag1.getName()))
			.andExpect(jsonPath("$.data[2].name").value(tag3.getName()));
	}

	@Test
	@DisplayName("인기 테마 조회 테스트")
	void getTop10PopularThemesByTagNameTest() throws Exception {
		// given
		String tagName = tag1.getName();

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/popular")
			.param("tagName", tagName)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(4))
			.andExpect(jsonPath("$.data[0].name").value(themes.get(0).getName()))
			.andExpect(jsonPath("$.data[0].tags[0]").value(tag1.getName()))
			.andExpect(jsonPath("$.data[0].tags[1]").value(tag2.getName()))
			.andExpect(jsonPath("$.data[1].name").value(themes.get(1).getName()))
			.andExpect(jsonPath("$.data[1].recommendedParticipants").value("2~3인"))
			.andExpect(jsonPath("$.data[2].name").value(themes.get(4).getName()));
	}

	@Test
	@DisplayName("없는 태그로 인기 테마 조회 테스트")
	void getTop10PopularThemesByInvalidTagNameTest() throws Exception {
		// given
		String tagName = "INVALID_TAG";

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/popular")
			.param("tagName", tagName)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(0));
	}

	@Test
	@DisplayName("최신 테마 조회 테스트")
	void getTop10NewestThemesByTagNameTest() throws Exception {
		// given
		String tagName = tag3.getName();

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/newest")
			.param("tagName", tagName)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(5))
			.andExpect(jsonPath("$.data[0].name").value(themes.get(27).getName()))
			.andExpect(jsonPath("$.data[0].tags[0]").value(tag3.getName()))
			.andExpect(jsonPath("$.data[1].name").value(themes.get(19).getName()))
			.andExpect(jsonPath("$.data[1].recommendedParticipants").value("2~3인"));
	}

	@Test
	@DisplayName("없는 태그로 최신 테마 조회 테스트")
	void getTop10NewestThemesByInvalidTagNameTest() throws Exception {
		// given
		String tagName = "INVALID_TAG";

		// when
		ResultActions result = mvc.perform(get("/api/v1/themes/newest")
			.param("tagName", tagName)
			.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(0));
	}
}
