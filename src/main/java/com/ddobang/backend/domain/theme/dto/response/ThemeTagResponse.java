package com.ddobang.backend.domain.theme.dto.response;

/**
 * ThemeTagResponse
 * 태그 목록 조회 응답 DTO
 * @author 100minha
 */
public record ThemeTagResponse(
	Long id,
	String name
) {
}
