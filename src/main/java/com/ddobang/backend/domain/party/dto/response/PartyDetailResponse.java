package com.ddobang.backend.domain.party.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;

import lombok.Builder;

@Builder
public record PartyDetailResponse(
	Long id,
	String title,
	LocalDateTime scheduledAt,
	String content,

	Long hostId,
	String hostNickname,
	String hostProfilePictureUrl,

	Integer acceptedParticipantsCount,
	Integer totalParticipants,

	List<PartyMemberSummaries> acceptedPartyMembers,
	List<PartyMemberSummaries> AppliedPartyMembers,

	Boolean rookieAvailable,

	Long themeId,
	String themeName,
	String themeThumbnailUrl,

	List<String> tagNames,

	float noHintEscapeRate,
	float escapeResult,
	float escapeTimeAvg,

	String storeName,
	String storeAddress
) {
	public static PartyDetailResponse from(Party party, ThemeStat themeStat, Member actor) {
		boolean isHost = party.getHost().getId().equals(actor.getId());
		Theme theme = party.getTheme();
		Member host = party.getHost();
		Store store = theme.getStore();

		return PartyDetailResponse.builder()
			.id(party.getId())
			.title(party.getTitle())
			.scheduledAt(party.getScheduledAt())
			.content(party.getContent())

			.hostId(host.getId())
			.hostNickname(host.getNickname())
			.hostProfilePictureUrl(host.getProfilePictureUrl())

			.acceptedParticipantsCount(
				party.getTotalParticipants() - party.getParticipantsNeeded() + party.getAcceptedParticipantsCount())
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

			.rookieAvailable(party.getRookieAvailable())

			.themeId(theme.getId())
			.themeName(theme.getName())
			.themeThumbnailUrl(theme.getThumbnailUrl())
			.tagNames(theme.getThemeTagMappings().stream()
				.map(ttm -> ttm.getThemeTag().getName())
				.collect(Collectors.toList()))

			.noHintEscapeRate(themeStat != null ? themeStat.getNoHintEscapeRate() : 0)
			.escapeResult(themeStat != null ? themeStat.getEscapeResult() : 0)
			.escapeTimeAvg(themeStat != null ? themeStat.getEscapeTimeAvg() : 0)

			.storeName(store.getName())
			.storeAddress(store.getAddress())
			.build();
	}
}
