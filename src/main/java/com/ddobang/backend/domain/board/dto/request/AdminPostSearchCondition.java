package com.ddobang.backend.domain.board.dto.request;

import com.ddobang.backend.domain.board.types.PostType;

public record AdminPostSearchCondition(
	PostType type,
	Boolean answered,
	Boolean deleted,
	String keyword
) {
}
