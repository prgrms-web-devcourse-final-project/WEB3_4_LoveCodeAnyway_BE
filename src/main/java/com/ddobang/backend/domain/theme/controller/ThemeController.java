package com.ddobang.backend.domain.theme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.theme.dto.ThemeDetailResponse;
import com.ddobang.backend.domain.theme.dto.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.ThemesResponse;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SliceDto;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/themes")
public class ThemeController {
	private final ThemeService themeService;

	@Operation(summary = "필터 기반 테마 다건 조회 api", description = "무한 스크롤에서 사용하기 위해 페이지네이션 처리(default = 0)")
	@PostMapping
	public ResponseEntity<SuccessResponse<SliceDto<ThemesResponse>>> getThemesWithFilter(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestBody @Valid ThemeFilterRequest filterRequest
	) {
		SliceDto<ThemesResponse> themes = themeService.getThemesWithFilter(filterRequest, page);

		return ResponseFactory.ok(themes);
	}

	@Operation(summary = "테마 상세 조회 api", description = "테마 통계 부분은 해당 테마에 대한 방탈출 일지가 1개 이상 작성 되야 존재하므로 Nullable")
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<ThemeDetailResponse>> getTheme(@PathVariable Long id) {
		ThemeDetailResponse themeDetailResponse = themeService.getTheme(id);

		return ResponseFactory.ok(themeDetailResponse);
	}

}
