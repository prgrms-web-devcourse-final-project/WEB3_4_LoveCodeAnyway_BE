package com.ddobang.backend.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostReplyRequest(
	@NotBlank
	String content
) {
}
