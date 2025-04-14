package com.ddobang.backend.global.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageDto<T>(
        int currentPageNumber,
        int pageSize,
        long totalPages,
        long totalItems,
        List<T> items
) {
    public static <T> PageDto<T> of(Page<T> page) {
        return new PageDto<>(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getContent()
        );
    }
}
