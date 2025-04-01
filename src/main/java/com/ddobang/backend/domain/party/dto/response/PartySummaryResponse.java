package com.ddobang.backend.domain.party.dto.response;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;

public record PartySummaryResponse(
	Long id,
	String title,

	LocalDateTime scheduled_at,

	Integer participantsNeeded,
	Integer totalParticipants,

	Boolean rookie_available,

	String store_name,

	Long theme_id,
	String theme_name,
	String theme_img_url,

	Long host_id,
	String host_nickname,
	String host_profile_img_url
) {
	public static PartySummaryResponse from(Party party) {
		Theme theme = party.getTheme();
		Member host = party.getHost();
		Store store = theme.getStore();
		return new PartySummaryResponse(
			party.getId(),
			party.getTitle(),

			party.getScheduledAt(),

			party.getParticipantsNeeded(),
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
