package com.ddobang.backend.domain.diary.dto.request;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record DiaryFilterRequest(
	List<Long> regionId,
	List<Long> tagIds,
	LocalDate startDate,
	LocalDate endDate,
	String isSuccess,
	Boolean isNoHint,
	String keyword
) {
	public boolean isInvalidDateRange() {
		return this.startDate() != null
			&& this.endDate() != null
			&& this.startDate().isAfter(this.endDate());
	}
}
