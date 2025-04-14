package com.ddobang.backend.domain.board.dto.response;

import com.ddobang.backend.domain.board.entity.Attachment;

public record AdminAttachmentResponse(
	Long id,
	String fileName,
	String url
) {
	public static AdminAttachmentResponse from(Attachment attachment) {
		return new AdminAttachmentResponse(
			attachment.getId(),
			attachment.getFileName(),
			attachment.getUrl()
		);
	}
}
