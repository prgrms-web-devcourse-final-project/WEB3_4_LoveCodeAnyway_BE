package com.ddobang.backend.domain.theme.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.service.StoreService;
import com.ddobang.backend.domain.theme.dto.request.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.request.ThemeForMemberRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeDetailResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeForPartyResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemesResponse;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.domain.theme.exception.ThemeErrorCode;
import com.ddobang.backend.domain.theme.exception.ThemeException;
import com.ddobang.backend.domain.theme.repository.ThemeRepository;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
import com.ddobang.backend.global.response.SliceDto;

/**
 * ThemeServiceTest
 * @author 100minha
 */
@ExtendWith(MockitoExtension.class)
public class ThemeServiceTest {

	@InjectMocks
	private ThemeService themeService;

	@Mock
	private ThemeRepository themeRepository;
	@Mock
	private ThemeStatRepository themeStatRepository;
	@Mock
	private StoreService storeService;
	@Mock
	private ThemeTagService themeTagService;

	private Store store = Store.builder()
		.name("매장1")
		.status(Store.Status.OPENED)
		.build();

	private final ThemeTag tag1 = new ThemeTag("태그1");
	private final ThemeTag tag2 = new ThemeTag("태그2");

	private final Theme theme = Theme.builder()
		.name("방탈출")
		.description("방탈출설명")
		.officialDifficulty(4.0f)
		.minParticipants(1)
		.maxParticipants(8)
		.status(Theme.Status.OPENED)
		.store(store)
		.themeTags(List.of(tag1, tag2))
		.thumbnailUrl("test.thumbnail")
		.build();

	private final ThemeStat stat = ThemeStat.builder()
		.theme(theme)
		.escapeResult(80)
		.build();

	@Test
	@DisplayName("필터 적용 테마 조회 성공")
	void getThemesWithFilter_success() {
		// given
		ThemeFilterRequest request = new ThemeFilterRequest(null, null, null, null);

		when(themeRepository.findThemesByFilter(request, 0, 5)).thenReturn(List.of(theme));

		// when
		SliceDto<ThemesResponse> result = themeService.getThemesWithFilter(request, 0, 5);

		// then
		assertThat(result.content()).hasSize(1);
		assertThat(result.content().get(0).name()).isEqualTo(theme.getName());
	}

	@Test
	@DisplayName("테마 상세 조회 성공 - 통계 포함")
	void getThemeWithStat_success() {
		// given
		when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
		when(themeStatRepository.findById(1L)).thenReturn(Optional.of(stat));

		// when
		ThemeDetailResponse response = themeService.getThemeWithStat(1L);

		// then
		assertThat(response.name()).isEqualTo(theme.getName());
		assertThat(response.diaryBasedThemeStat().escapeResult()).isEqualTo(stat.getEscapeResult());
	}

	@Test
	@DisplayName("테마 상세 조회 실패 - 존재하지 않는 ID")
	void getThemeWithStat_fail() {
		// given
		when(themeRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when
		ThemeErrorCode errorCode = ThemeErrorCode.THEME_NOT_FOUND;

		// then
		assertThatThrownBy(() -> themeService.getThemeWithStat(1L))
			.isInstanceOf(ThemeException.class)
			.hasMessageContaining(errorCode.getMessage());
	}

	@Test
	@DisplayName("모임 등록용 테마 검색 성공")
	void getThemesForPartySearch_success() {
		String keyword = "방탈";
		when(themeRepository.findThemesForPartySearch(keyword)).thenReturn(List.of(theme));

		List<ThemeForPartyResponse> result = themeService.getThemesForPartySearch(keyword);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).name()).isEqualTo(theme.getName());
	}

	@Test
	@DisplayName("일지 등록용 테마 검색 성공")
	void getThemesForDiarySearch_success() {
		String keyword = "방탈";
		SimpleThemeResponse response = new SimpleThemeResponse(1L, "일지테마", "매장1");
		when(themeRepository.findThemesForDiarySearch(keyword)).thenReturn(List.of(response));

		List<SimpleThemeResponse> result = themeService.getThemesForDiarySearch(keyword);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).themeName()).isEqualTo("일지테마");
	}

	@Test
	@DisplayName("테마 ID로 조회 성공")
	void getThemeById_success() {
		when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));

		Theme result = themeService.getThemeById(1L);

		assertThat(result.getName()).isEqualTo(theme.getName());
	}

	@Test
	@DisplayName("테마 ID로 조회 실패")
	void getThemeById_fail() {
		// given
		when(themeRepository.findById(1L)).thenReturn(Optional.empty());

		// when
		ThemeErrorCode errorCode = ThemeErrorCode.THEME_NOT_FOUND;
		// then
		assertThatThrownBy(() -> themeService.getThemeById(1L))
			.isInstanceOf(ThemeException.class)
			.hasMessageContaining(errorCode.getMessage());
	}

	@Test
	@DisplayName("사용자 전용 테마 저장 성공")
	void saveThemeForMember_success() {
		// given
		ThemeForMemberRequest request = new ThemeForMemberRequest(
			"테마A", "매장A", "url", List.of(1L));

		when(themeTagService.getTagsByIds(anyList())).thenReturn(List.of(tag1));
		when(storeService.saveForMember(any())).thenReturn(store);
		when(themeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		SimpleThemeResponse response = themeService.saveForMember(request);

		assertThat(response.themeName()).isEqualTo("테마A");
		assertThat(response.storeName()).isEqualTo("매장1");
	}
}
