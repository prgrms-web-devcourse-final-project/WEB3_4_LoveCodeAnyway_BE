package com.ddobang.backend.domain.board.dto.response;

import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.types.PostType;

import java.time.LocalDateTime;

public record PostSummaryResponse(
        Long id,
        PostType type,
        String title,
        boolean answered,
        boolean hasAttachments,
        LocalDateTime createdAt
) {
    public static PostSummaryResponse of(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getType(),
                post.getTitle(),
                post.isAnswered(),
                !post.getAttachments().isEmpty(),
                post.getCreatedAt()
        );
    }
}
