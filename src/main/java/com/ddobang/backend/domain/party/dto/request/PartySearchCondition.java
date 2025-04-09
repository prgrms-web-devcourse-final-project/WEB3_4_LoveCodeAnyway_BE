package com.ddobang.backend.domain.party.dto.request;

import java.time.LocalDate;
import java.util.List;

public record PartySearchCondition(
	String keyword,
	List<Long> regionIds,
	List<LocalDate> dates,
	List<Long> tagsIds
) {
}
