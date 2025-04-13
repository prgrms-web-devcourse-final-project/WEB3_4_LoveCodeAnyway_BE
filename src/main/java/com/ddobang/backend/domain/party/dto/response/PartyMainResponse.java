package com.ddobang.backend.domain.party.dto.response;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;

public record PartyMainResponse(
	Long themeId,
	String themeName,
	String themeThumbnailUrl,

	Long storeId,
	String storeName,

	Long id,
	String title,
	LocalDateTime scheduledAt,

	int acceptedParticipantCount,
	int totalParticipants
) {
	public static PartyMainResponse from(Party party) {
		Theme theme = party.getTheme();
		Store store = theme.getStore();
		return new PartyMainResponse(
			theme.getId(),
			theme.getName(),
			theme.getThumbnailUrl(),

			store.getId(),
			store.getName(),

			party.getId(),
			party.getTitle(),
			party.getScheduledAt(),

			party.getTotalParticipants() - party.getParticipantsNeeded() + party.getAcceptedParticipantsCount(),
			party.getTotalParticipants()
		);
	}
}
