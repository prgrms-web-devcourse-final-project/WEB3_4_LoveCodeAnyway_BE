package com.ddobang.backend.domain.member.types;

import static com.ddobang.backend.domain.member.types.KeywordType.*;

import lombok.Getter;

@Getter
public enum MemberReviewKeyword {
	ATTENDANCE(POSITIVE),
	COMMUNICATION(POSITIVE),
	COOPERATION(POSITIVE),
	INTUITION(POSITIVE),
	LEADERSHIP(POSITIVE),

	LATE(NEGATIVE),
	PASSIVE(NEGATIVE),
	SELF_CENTERED(NEGATIVE),
	OFF_TOPIC(NEGATIVE),
	RUDE(NEGATIVE),

	NO_SHOW(NOSHOW);

	private final KeywordType type;

	MemberReviewKeyword(KeywordType type) {
		this.type = type;
	}
}
