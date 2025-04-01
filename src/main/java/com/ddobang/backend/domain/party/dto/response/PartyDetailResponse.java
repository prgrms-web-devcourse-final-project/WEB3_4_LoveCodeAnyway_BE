package com.ddobang.backend.domain.party.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.types.PartyMemberRole;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeTagMapping;

public record PartyDetailResponse(
	Long id,
	String title,
	LocalDateTime scheduled_at,
	String content,

	Long host_id,
	String host_nickname,
	String host_profile_img_url,

	Integer participantsNeeded,
	Integer totalParticipants,

	List<PartyMemberSummaries> acceptedPartyMembers,

	List<PartyMemberSummaries> AppliedPartyMembers,

	Boolean rookie_available,

	Long theme_id,
	String theme_name,
	String theme_img_url,

	List<ThemeTagMapping> theme_tag_mappings,

	// float noHintEscapeRate,
	// float escapeResult,
	// float escapeTimeAvg,

	String store_name,
	String store_address
) {
	public static PartyDetailResponse from(Party party, Member actor) {
		boolean isHost = party.getPartyMemberRole(actor).equals(PartyMemberRole.HOST);
		Theme theme = party.getTheme();
		// ThemeStat themeStats = theme.getThemeStat();
		Member host = party.getHost();
		Store store = theme.getStore();
		return new PartyDetailResponse(
			party.getId(),
			party.getTitle(),
			party.getScheduledAt(),
			party.getContent(),

			host.getId(),
			host.getNickname(),
			host.getProfilePictureUrl(),

			party.getParticipantsNeeded(),
			party.getTotalParticipants(),

			party.getAcceptedMembers().stream()
				.map(PartyMemberSummaries::from)
				.toList(),

			isHost ? party.getApplicants().stream()
				.map(PartyMemberSummaries::from)
				.toList() : null,

			party.getRookieAvailable(),

			theme.getId(),
			theme.getName(),
			theme.getThumbnailUrl(),
			theme.getThemeTagMappings(),

			// themeStats.getNoHintEscapeRate(),
			// themeStats.getEscapeResult(),
			// themeStats.getEscapeTimeAvg(),

			store.getName(),
			store.getAddress()
		);
	}
}
