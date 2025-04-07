package com.ddobang.backend.domain.party.dto.response;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.PartyMember;

public record PartyMemberSummaries(
	Long id,
	String profilePictureUrl,
	String nickname
) {
	public static PartyMemberSummaries from(PartyMember partyMember) {
		Member member = partyMember.getMember();
		return new PartyMemberSummaries(
			member.getId(),
			member.getProfilePictureUrl(),
			member.getNickname()
		);
	}
}
