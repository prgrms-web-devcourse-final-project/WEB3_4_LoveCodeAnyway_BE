package com.ddobang.backend.domain.theme.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.service.StoreService;
import com.ddobang.backend.domain.theme.dto.ThemeStatDto;
import com.ddobang.backend.domain.theme.dto.request.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.request.ThemeForAdminRequest;
import com.ddobang.backend.domain.theme.dto.request.ThemeForMemberRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeDetailResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeForAdminResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeForPartyResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeTagResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemesResponse;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.domain.theme.exception.ThemeErrorCode;
import com.ddobang.backend.domain.theme.exception.ThemeException;
import com.ddobang.backend.domain.theme.repository.ThemeRepository;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
import com.ddobang.backend.global.response.SliceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeService {

	private final ThemeRepository themeRepository;
	private final ThemeStatRepository themeStatRepository;

	private final StoreService storeService;
	private final ThemeTagService themeTagService;

	@Transactional(readOnly = true)
	public SliceDto<ThemesResponse> getThemesWithFilter(ThemeFilterRequest filterRequest, int page, int size) {

		List<Theme> themes = themeRepository.findThemesByFilter(filterRequest, page, size);

		return SliceDto.of(themes.stream().map(ThemesResponse::of).toList(), size);
	}

	@Transactional(readOnly = true)
	public ThemeDetailResponse getThemeWithStat(Long id) {

		Theme theme = themeRepository.findById(id)
			.orElseThrow(() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND));

		// 테마 통계가 등록되어있다면 통계 반영, 없다면 통계 부분은 null 반환
		ThemeStatDto themeStatDto = themeStatRepository.findById(id).map(ThemeStatDto::of).orElse(null);

		return ThemeDetailResponse.of(theme, themeStatDto);
	}

	@Transactional(readOnly = true)
	public List<ThemeForPartyResponse> getThemesForPartySearch(String keyword) {

		List<Theme> themes = themeRepository.findThemesForPartySearch(keyword);

		return themes.stream().map(ThemeForPartyResponse::of).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<SimpleThemeResponse> getThemesForDiarySearch(String keyword) {

		return themeRepository.findThemesForDiarySearch(keyword);
	}

	public Theme getThemeById(Long id) {
		return themeRepository.findById(id).orElseThrow(() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND));
	}

	/**
	 * 사용자 일지 등록 전용 테마 저장 메서드
	 * @param request ThemeForMemberRequest
	 */
	@Transactional
	public SimpleThemeResponse saveForMember(ThemeForMemberRequest request) {
		List<ThemeTag> themeTags = themeTagService.getTagsByIds(request.tagIds());
		Store store = storeService.saveForMember(Store.builder().name(request.storeName()).build());

		Theme savedTheme = themeRepository.save(Theme.builder()
			.name(request.themeName())
			.store(store)
			.thumbnailUrl(request.thumbnailUrl())
			.status(Theme.Status.INACTIVE)
			.themeTags(themeTags)
			.build());

		return SimpleThemeResponse.of(savedTheme);
	}

	@Transactional
	public void saveForAdmin(ThemeForAdminRequest request) {
		List<ThemeTag> themeTags = themeTagService.getTagsByIds(request.tagIds());
		Store store = storeService.findById(request.storeId());

		themeRepository.save(Theme.of(request, store, themeTags));
	}

	@Transactional
	public void modify(Long id, ThemeForAdminRequest request) {
		Theme theme = getThemeById(id);
		List<ThemeTag> themeTags = themeTagService.getTagsByIds(request.tagIds());
		Store store = storeService.findById(request.storeId());

		theme.modify(request, store, themeTags);
	}

	@Transactional
	public void delete(Long id) {
		Theme theme = getThemeById(id);

		theme.delete();
	}

	@Transactional(readOnly = true)
	public ThemeForAdminResponse getThemeForAdmin(Long id) {
		Theme theme = getThemeById(id);

		return ThemeForAdminResponse.of(theme);
	}

	@Transactional(readOnly = true)
	public SliceDto<SimpleThemeResponse> getThemesWithFilterForAdmin(ThemeFilterRequest filterRequest, int page,
		int size) {
		List<SimpleThemeResponse> themes = themeRepository.findThemesForAdminSearch(filterRequest, page, size);
		return SliceDto.of(themes, size);
	}

	public List<ThemeTagResponse> getAllThemeTags() {
		return themeTagService.getAllTags();
	}

	@Transactional(readOnly = true)
	public ThemeStat getThemeStatById(Long id) {
		return themeStatRepository.findById(id).orElse(null);
	}

	@Cacheable(cacheNames = "popularThemesByTag", key = "#tagName")
	@Transactional(readOnly = true)
	public List<ThemesResponse> getPopularThemesByTagName(String tagName) {
		List<Theme> themes = themeRepository.findTop10PopularThemesByTagName(tagName);

		return themes.stream().map(ThemesResponse::of).toList();
	}

	@Cacheable(cacheNames = "newestThemesByTag", key = "#tagName")
	@Transactional(readOnly = true)
	public List<ThemesResponse> getNewestThemesByTagName(String tagName) {
		List<Theme> themes = themeRepository.findTop10NewestThemesByTagName(tagName);

		return themes.stream().map(ThemesResponse::of).toList();
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
	@CacheEvict(cacheNames = {"popularThemesByTag", "newestThemesByTag"}, allEntries = true)
	public void clearThemeCachesDaily() {
		log.info("매일 자정 캐시 초기화: 인기 테마 / 최신 테마 캐시 삭제 완료");
	}
}
