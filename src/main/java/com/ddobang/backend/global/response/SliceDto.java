package com.ddobang.backend.global.response;

import java.util.List;

public record SliceDto<T>(
	List<T> content,
	boolean hasNext
) {
	public static <T> SliceDto<T> of(List<T> content, int size) {
		boolean hasNext = content.size() > size;
		if (hasNext) {
			content = content.subList(0, size);
		}
		return new SliceDto<>(content, hasNext);
	}
}
