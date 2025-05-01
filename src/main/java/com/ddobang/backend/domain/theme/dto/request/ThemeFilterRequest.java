package com.ddobang.backend.domain.theme.dto.request;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * ThemeFilterRequest
 * 테마 다건 조회 필터 body
 * @author 100minha
 */
public record ThemeFilterRequest(
	List<Long> regionId,
	List<Long> tagIds,
	@Min(1) @Max(8)
	Integer participants,
	String keyword
) {
}
