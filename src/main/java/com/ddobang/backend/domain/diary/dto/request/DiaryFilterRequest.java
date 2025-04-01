package com.ddobang.backend.domain.diary.dto.request;

import java.time.LocalDate;
import java.util.List;

public record DiaryFilterRequest(
	List<Long> regionId,
	List<String> tagNames,
	LocalDate startDate,
	LocalDate endDate,
	String isSuccess,
	Boolean isNoHint,
	String keyword
) {
}
