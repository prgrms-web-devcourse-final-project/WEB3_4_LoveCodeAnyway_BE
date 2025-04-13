package com.ddobang.backend.domain.party.dto.response;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;

public record PartySummaryResponse(
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
	String hostProfilePictureUrl
) {
	public static PartySummaryResponse from(Party party) {
		Theme theme = party.getTheme();
		Member host = party.getHost();
		Store store = theme.getStore();

		return new PartySummaryResponse(
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
			host.getProfilePictureUrl()
		);
	}
}
