package com.ddobang.backend.domain.party.dto.request;

import java.util.List;

public record PartyMemberReviewRequest(
	Long targetId,
	List<String> reviewKeywords,
	boolean noShow
) {
}
