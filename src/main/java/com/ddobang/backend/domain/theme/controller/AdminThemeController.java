package com.ddobang.backend.domain.theme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.theme.dto.request.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.request.ThemeForAdminRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.dto.response.ThemeForAdminResponse;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SliceDto;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * AdminThemeController
 * 관리자 전용 테마 관리 API 핸들러
 * @author 100minha
 */
@Tag(name = "AdminThemeController", description = "관리자 전용 테마 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/themes")
public class AdminThemeController {
	private final ThemeService themeService;

	@Operation(summary = "관리자 전용 테마 등록 API")
	@PostMapping
	public ResponseEntity<SuccessResponse<Void>> save(
		@RequestBody @Valid ThemeForAdminRequest request) {
		themeService.saveForAdmin(request);
		return ResponseFactory.created("테마 등록에 성공했습니다.");
	}

	@Operation(summary = "관리자 전용 테마 단건 조회 API")
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<ThemeForAdminResponse>> getTheme(
		@PathVariable Long id) {
		ThemeForAdminResponse theme = themeService.getThemeForAdmin(id);
		return ResponseFactory.ok(theme);
	}

	@Operation(summary = "관리자 전용 테마 필터 + 검색 API")
	@PostMapping("/search")
	public ResponseEntity<SuccessResponse<SliceDto<SimpleThemeResponse>>> getThemes(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "10") int size,
		@RequestBody @Valid ThemeFilterRequest filterRequest
	) {
		SliceDto<SimpleThemeResponse> themes = themeService.getThemesWithFilterForAdmin(filterRequest, page, size);
		return ResponseFactory.ok(themes);
	}

	@Operation(summary = "관리자 전용 테마 수정 API")
	@PutMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> modify(
		@PathVariable Long id,
		@RequestBody @Valid ThemeForAdminRequest request) {
		themeService.modify(id, request);
		return ResponseFactory.created(id + "번 테마 수정에 성공했습니다.");
	}

	@Operation(summary = "관리자 전용 테마 삭제 API")
	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> delete(
		@PathVariable Long id) {
		themeService.delete(id);
		return ResponseFactory.created(id + "번 테마 삭제에 성공했습니다.");
	}
}
