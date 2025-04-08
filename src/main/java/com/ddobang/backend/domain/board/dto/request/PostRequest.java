package com.ddobang.backend.domain.board.dto.request;

import com.ddobang.backend.domain.board.entity.Attachment;
import com.ddobang.backend.domain.board.types.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostRequest(
        @NotNull
        PostType type,

        @NotBlank
        @Size(max = 100)
        String title,

        @NotBlank
        @Size(max = 5000)
        String content,

        List<Attachment> attachments
) {
}
