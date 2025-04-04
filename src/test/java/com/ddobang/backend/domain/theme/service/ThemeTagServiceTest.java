package com.ddobang.backend.domain.theme.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.domain.theme.exception.ThemeException;
import com.ddobang.backend.domain.theme.repository.ThemeTagRepository;

/**
 * ThemeTagServiceTest
 * @author 100minha
 */
@ExtendWith(MockitoExtension.class)
public class ThemeTagServiceTest {

	@InjectMocks
	private ThemeTagService themeTagService;

	@Mock
	private ThemeTagRepository themeTagRepository;

	@Test
	@DisplayName("태그 이름으로 조회 성공")
	void getByName_success() {
		// given
		String tagName = "공포";
		ThemeTag tag = new ThemeTag(tagName);

		when(themeTagRepository.findByName(tagName)).thenReturn(Optional.of(tag));

		// when
		ThemeTag result = themeTagService.getByName(tagName);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(tagName);
		verify(themeTagRepository).findByName(tagName);
	}

	@Test
	@DisplayName("태그 이름으로 조회 실패 - 존재하지 않는 태그")
	void getByName_fail_tagNotFound() {
		// given
		String tagName = "없는태그";

		when(themeTagRepository.findByName(tagName)).thenReturn(Optional.empty());

		// when
		assertThrows(ThemeException.class, () -> themeTagService.getByName(tagName));

		// then
		verify(themeTagRepository).findByName(tagName);
	}
}
