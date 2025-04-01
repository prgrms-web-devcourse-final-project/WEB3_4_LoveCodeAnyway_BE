package com.ddobang.backend.domain.theme.dto;

/**
 * ThemesDto
 * 테마 다건 조회 쿼리에서 반환 타입으로 사용할 dto
 * @author 100minha
 */
public record ThemesDto(
	Long id,
	String name,
	String storeName,
	int runtime,
	int minParticipants,
	int maxParticipants,
	String thumbnailUrl
) {

}
