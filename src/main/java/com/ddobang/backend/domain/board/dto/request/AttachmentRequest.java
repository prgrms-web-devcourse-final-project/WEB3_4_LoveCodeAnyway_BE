package com.ddobang.backend.domain.board.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AttachmentRequest(
        @Size(max = 512)
        @Pattern(regexp = "^(https?|ftp)://[^\s/$.?#].[^\s]*$")
        String url,

        @Size(max = 255)
        String originalName
) {
}
