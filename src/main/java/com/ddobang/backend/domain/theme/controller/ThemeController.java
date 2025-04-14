package com.ddobang.backend.domain.theme.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.service.PartyService;
import com.ddobang.backend.domain.theme.dto.request.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeDetailResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeForPartyResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeTagResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemesResponse;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SliceDto;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "ThemeController", description = "테마 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/themes")
public class ThemeController {
	private final ThemeService themeService;
	private final PartyService partyService;

	@Operation(summary = "필터 기반 테마 다건 조회 api", description = "무한 스크롤에서 사용하기 위해 페이지네이션 처리(default = 0)")
	@PostMapping
	public ResponseEntity<SuccessResponse<SliceDto<ThemesResponse>>> getThemesWithFilter(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "5") int size,
		@RequestBody @Valid ThemeFilterRequest filterRequest
	) {
		SliceDto<ThemesResponse> themes = themeService.getThemesWithFilter(filterRequest, page, size);

		return ResponseFactory.ok(themes);
	}

	@Operation(summary = "테마 상세 조회 api", description = "테마 통계 부분은 해당 테마에 대한 방탈출 일지가 1개 이상 작성 되야 존재하므로 Nullable")
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<ThemeDetailResponse>> getTheme(@PathVariable Long id) {
		ThemeDetailResponse themeDetailResponse = themeService.getThemeWithStat(id);

		return ResponseFactory.ok(themeDetailResponse);
	}

	@Operation(summary = "모임 등록 전용 테마 검색 api", description = "운영중인 테마만 검색 가능")
	@GetMapping("/search-for-party")
	public ResponseEntity<SuccessResponse<List<ThemeForPartyResponse>>> getThemesForPartySearch(
		@RequestParam(name = "keyword") String keyword
	) {
		List<ThemeForPartyResponse> themesForParty = themeService.getThemesForPartySearch(keyword);

		return ResponseFactory.ok(themesForParty);
	}

	@Operation(summary = "방탈출 일지 작성 전용 테마 검색 api", description = "삭제된(soft) 테마 제외 모든 테마 검색 가능")
	@GetMapping("/search-for-diary")
	public ResponseEntity<SuccessResponse<List<SimpleThemeResponse>>> getThemesForDiarySearch(
		@RequestParam(name = "keyword") String keyword
	) {
		List<SimpleThemeResponse> themesForDiary = themeService.getThemesForDiarySearch(keyword);

		return ResponseFactory.ok(themesForDiary);
	}

	@Operation(summary = "태그 목록 조회 api")
	@GetMapping("/tags")
	ResponseEntity<SuccessResponse<List<ThemeTagResponse>>> getAllThemeTags() {
		List<ThemeTagResponse> themeTags = themeService.getAllThemeTags();

		return ResponseFactory.ok(themeTags);
	}

	@Operation(summary = "태그별 인기 테마 Top 10 조회 api")
	@GetMapping("/popular")
	ResponseEntity<SuccessResponse<List<ThemesResponse>>> getPopularThemes(
		@RequestParam String tagName
	) {
		List<ThemesResponse> popularThemes = themeService.getPopularThemesByTagName(tagName);

		return ResponseFactory.ok(popularThemes);
	}

	@Operation(summary = "태그별 최신 테마 Top 10 조회 api")
	@GetMapping("/newest")
	ResponseEntity<SuccessResponse<List<ThemesResponse>>> getNewestThemes(
		@RequestParam String tagName
	) {
		List<ThemesResponse> newestThemes = themeService.getNewestThemesByTagName(tagName);

		return ResponseFactory.ok(newestThemes);
	}

	@GetMapping("/{id}/parties")
	@Operation(summary = "해당 테마의 모집 중인 모임 조회")
	public ResponseEntity<SuccessResponse<SliceDto<PartySummaryResponse>>> getPartiesByTheme(
		@PathVariable Long id,
		@RequestParam(required = false) Long lastId,
		@RequestParam(defaultValue = "10") int size
	) {
		SliceDto<PartySummaryResponse> result = partyService.getPartiesByTheme(id, lastId, size);
		return ResponseEntity.ok(SuccessResponse.of(result));
	}
}
