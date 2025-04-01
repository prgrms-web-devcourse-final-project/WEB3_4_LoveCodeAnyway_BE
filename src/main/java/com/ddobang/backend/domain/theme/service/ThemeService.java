package com.ddobang.backend.domain.theme.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.theme.dto.ThemeDetailResponse;
import com.ddobang.backend.domain.theme.dto.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.ThemeStatDto;
import com.ddobang.backend.domain.theme.dto.ThemesResponse;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.exception.ThemeErrorCode;
import com.ddobang.backend.domain.theme.exception.ThemeException;
import com.ddobang.backend.domain.theme.repository.ThemeRepository;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
import com.ddobang.backend.global.response.SliceDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemeService {

	private final ThemeRepository themeRepository;
	private final ThemeStatRepository themeStatRepository;

	@Transactional(readOnly = true)
	public SliceDto<ThemesResponse> getThemesWithFilter(ThemeFilterRequest filterRequest, int page) {
		int size = 8;
		List<Theme> themes = themeRepository.findThemesByFilter(filterRequest, page, size);

		return SliceDto.of(themes.stream()
			.map(ThemesResponse::of)
			.toList(), size);
	}

	@Transactional(readOnly = true)
	public ThemeDetailResponse getTheme(Long id) {
		Theme theme = themeRepository.findById(id).orElseThrow(
			() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND)
		);

		// 테마 통계가 등록되어있다면 통계 반영, 없다면 통계 부분은 null 반환
		ThemeStatDto themeStatDto = themeStatRepository.findById(id)
			.map(ThemeStatDto::of)
			.orElse(null);

		return ThemeDetailResponse.of(theme, themeStatDto);
	}
}
