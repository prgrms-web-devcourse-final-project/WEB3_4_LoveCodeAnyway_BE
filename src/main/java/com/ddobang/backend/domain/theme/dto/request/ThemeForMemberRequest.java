package com.ddobang.backend.domain.theme.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

/**
 * ThemeForMemberRequest
 * 사용자 전용 테마 등록 요청 DTO
 * @author 100minha
 */
public record ThemeForMemberRequest(
	@NotBlank(message = "테마 이름은 공백일 수 없습니다.")
	String themeName,
	@NotBlank(message = "매장 이름은 공백일 수 없습니다.")
	String storeName,
	String thumbnailUrl,
	List<Long> tagIds
) {
}
