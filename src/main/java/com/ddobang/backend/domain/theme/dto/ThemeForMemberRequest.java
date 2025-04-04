package com.ddobang.backend.domain.theme.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

/**
 * ThemeForMemberRequest
 * 사용자 전용 테마 등록 요청 DTO
 * @author 100minha
 */
public record ThemeForMemberRequest(
	@NotBlank
	String themeName,
	@NotBlank
	String storeName,
	String thumbnailUrl,
	List<String> tags
) {
}
