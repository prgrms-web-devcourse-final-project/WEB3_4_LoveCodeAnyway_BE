package com.ddobang.backend.domain.member.dto.request;

import java.util.List;

import jakarta.validation.constraints.Size;

// 사용자 태그 수정 요청 DTO
public record UpdateTagsRequest(
	@Size(max = 5, message = "태그는 최대 5개까지만 선택할 수 있습니다.")
	List<Long> tagIds
) {
}
