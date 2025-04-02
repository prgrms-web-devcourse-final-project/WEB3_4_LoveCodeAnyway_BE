package com.ddobang.backend.domain.party.dto.response;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;

import lombok.Builder;

@Builder
public record PartySummaryResponse(
	Long id,
	String title,

	LocalDateTime scheduled_at,

	Integer recruitableCount,
	Integer totalParticipants,

	Boolean rookie_available,

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

		return PartySummaryResponse.builder()
			.id(party.getId())
			.title(party.getTitle())
			.scheduled_at(party.getScheduledAt())
			.recruitableCount(party.getParticipantsNeeded() - party.getAcceptedParticipantsCount())
			.totalParticipants(party.getTotalParticipants())
			.rookie_available(party.getRookieAvailable())
			.store_name(store.getName())
			.theme_id(theme.getId())
			.theme_name(theme.getName())
			.theme_thumbnail_url(theme.getThumbnailUrl())
			.host_id(host.getId())
			.host_nickname(host.getNickname())
			.host_profile_picture_url(host.getProfilePictureUrl())
			.build();
	}
}
