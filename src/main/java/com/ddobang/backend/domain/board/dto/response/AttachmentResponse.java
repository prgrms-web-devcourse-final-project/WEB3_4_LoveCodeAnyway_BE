package com.ddobang.backend.domain.board.dto.response;

import com.ddobang.backend.domain.board.entity.Attachment;

public record AttachmentResponse(
	Long id,
	String fileName
) {
	public static AttachmentResponse from(Attachment attachment) {
		return new AttachmentResponse(
			attachment.getId(),
			attachment.getFileName()
		);
	}
}
