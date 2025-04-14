package com.ddobang.backend.domain.theme.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.theme.dto.response.ThemeTagResponse;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.domain.theme.exception.ThemeErrorCode;
import com.ddobang.backend.domain.theme.exception.ThemeException;
import com.ddobang.backend.domain.theme.repository.ThemeTagRepository;

import lombok.RequiredArgsConstructor;

/**
 * ThemeTagService
 * 테마 태그 서비스 로직
 * @author 100minha
 */
@Service
@RequiredArgsConstructor
public class ThemeTagService {

	private final ThemeTagRepository themeTagRepository;

	@Transactional(readOnly = true)
	public ThemeTag getByName(String name) {
		return themeTagRepository.findByName(name)
			.orElseThrow(() -> new ThemeException(ThemeErrorCode.THEME_TAG_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<ThemeTag> getTagsByIds(List<Long> ids) {
		return themeTagRepository.findAllByIdIn(ids);
	}

	@Transactional(readOnly = true)
	public List<ThemeTagResponse> getAllTags() {
		return themeTagRepository.findAllTags();
	}
}
