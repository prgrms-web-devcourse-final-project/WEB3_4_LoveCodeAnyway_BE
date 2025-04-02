package com.ddobang.backend.domain.party.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.types.PartyMemberRole;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.entity.ThemeTagMapping;

import lombok.Builder;

@Builder
public record PartyDetailResponse(
	Long id,
	String title,
	LocalDateTime scheduled_at,
	String content,

	Long host_id,
	String host_nickname,
	String host_profile_img_url,

	Integer recruitableCount,
	Integer totalParticipants,

	List<PartyMemberSummaries> acceptedPartyMembers,
	List<PartyMemberSummaries> AppliedPartyMembers,

	Boolean rookie_available,

	Long theme_id,
	String theme_name,
	String theme_thumbnail_url,

	List<ThemeTagMapping> theme_tag_mappings,

	float no_hint_escape_rate,
	float escape_result,
	float escape_time_avg,

	String store_name,
	String store_address
) {
	public static PartyDetailResponse from(Party party, ThemeStat themeStat, Member actor) {
		boolean isHost = party.getPartyMemberRole(actor).equals(PartyMemberRole.HOST);
		Theme theme = party.getTheme();
		Member host = party.getHost();
		Store store = theme.getStore();

		return PartyDetailResponse.builder()
			.id(party.getId())
			.title(party.getTitle())
			.scheduled_at(party.getScheduledAt())
			.content(party.getContent())

			.host_id(host.getId())
			.host_nickname(host.getNickname())
			.host_profile_img_url(host.getProfilePictureUrl())

			.recruitableCount(party.getParticipantsNeeded() - party.getAcceptedParticipantsCount())
			.totalParticipants(party.getTotalParticipants())

			.acceptedPartyMembers(
				party.getAcceptedMembers().stream()
					.map(PartyMemberSummaries::from)
					.toList()
			)
			.AppliedPartyMembers(
				isHost ? party.getApplicants().stream()
					.map(PartyMemberSummaries::from)
					.toList() : null
			)

			.rookie_available(party.getRookieAvailable())

			.theme_id(theme.getId())
			.theme_name(theme.getName())
			.theme_thumbnail_url(theme.getThumbnailUrl())
			.theme_tag_mappings(theme.getThemeTagMappings())

			.no_hint_escape_rate(themeStat.getNoHintEscapeRate())
			.escape_result(themeStat.getEscapeResult())
			.escape_time_avg(themeStat.getEscapeTimeAvg())

			.store_name(store.getName())
			.store_address(store.getAddress())
			.build();
	}
}
