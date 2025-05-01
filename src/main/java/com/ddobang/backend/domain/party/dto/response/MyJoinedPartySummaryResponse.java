package com.ddobang.backend.domain.party.dto.response;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.types.PartyMemberRole;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;

public record MyJoinedPartySummaryResponse(
	Long partyId,
	String title,
	LocalDateTime scheduledAt,
	Integer acceptedParticipantsCount,
	Integer totalParticipants,
	Boolean rookieAvailable,
	String storeName,
	Long themeId,
	String themeName,
	String themeThumbnailUrl,
	Long hostId,
	String hostNickname,
	String hostProfilePictureUrl,
	PartyMemberRole role,
	Boolean reviewed,
	PartyStatus status
) {
	public static MyJoinedPartySummaryResponse from(Party party, Member actor, boolean reviewed) {
		Theme theme = party.getTheme();
		Member host = party.getHost();
		Store store = theme.getStore();

		return new MyJoinedPartySummaryResponse(
			party.getId(),
			party.getTitle(),
			party.getScheduledAt(),
			party.getTotalParticipants() - party.getParticipantsNeeded() + party.getAcceptedParticipantsCount(),
			party.getTotalParticipants(),
			party.getRookieAvailable(),
			store.getName(),
			theme.getId(),
			theme.getName(),
			theme.getThumbnailUrl(),
			host.getId(),
			host.getNickname(),
			host.getProfilePictureUrl(),

			party.getPartyMemberRole(actor),
			reviewed,
			party.getStatus()
		);
	}
}
