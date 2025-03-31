package com.ddobang.backend.domain.theme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.theme.dto.ThemeDetailResponse;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/themes")
public class ThemeController {
	private final ThemeService themeService;

	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<ThemeDetailResponse>> getTheme(@PathVariable Long id) {
		ThemeDetailResponse themeDetailResponse = themeService.getTheme(id);

		return ResponseFactory.ok(themeDetailResponse);
	}

}
