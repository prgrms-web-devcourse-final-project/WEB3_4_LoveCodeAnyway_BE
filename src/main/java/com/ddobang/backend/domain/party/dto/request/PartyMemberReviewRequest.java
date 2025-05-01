package com.ddobang.backend.domain.party.dto.request;

import java.util.List;

public record PartyMemberReviewRequest(
	String targetNickname,
	List<String> reviewKeywords,
	boolean noShow
) {
}
