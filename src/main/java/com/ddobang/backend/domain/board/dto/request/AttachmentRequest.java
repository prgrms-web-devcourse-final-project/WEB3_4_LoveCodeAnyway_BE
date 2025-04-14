package com.ddobang.backend.domain.board.dto.request;

import jakarta.validation.constraints.Size;

public record AttachmentRequest(
	@Size(max = 512)
	String url,

	@Size(max = 255)
	String originalName
) {
}
