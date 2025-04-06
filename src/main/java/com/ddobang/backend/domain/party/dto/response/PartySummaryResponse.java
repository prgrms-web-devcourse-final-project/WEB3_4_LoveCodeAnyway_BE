package com.ddobang.backend.domain.party.dto.response;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;

import java.time.LocalDateTime;

public record PartySummaryResponse(
		Long party_id,
		String title,
		LocalDateTime scheduled_at,
		int participants_left,
		int total_participants,
		boolean rookie_available,
		String store_name,
		Long theme_id,
		String theme_name,
		String theme_thumbnail_url,
		Long host_id,
		String host_nickname,
		String host_profile_picture_url
) {
	public static PartySummaryResponse from(Party party) {
		Theme theme = party.getTheme();
		Member host = party.getHost();
		Store store = theme.getStore();

		return new PartySummaryResponse(
				party.getId(),
				party.getTitle(),
				party.getScheduledAt(),
				party.getParticipantsNeeded() - party.getAcceptedParticipantsCount(),
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
