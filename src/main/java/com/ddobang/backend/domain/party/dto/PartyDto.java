package com.ddobang.backend.domain.party.dto;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.types.PartyStatus;

public record PartyDto(
	Long id,
	String title,
	String content,
	LocalDateTime scheduledAt,
	Integer participantsNeeded,
	Integer acceptedParticipantsCount,
	Integer totalParticipants,
	Boolean rookieAvailable,
	PartyStatus status,

	Long host_id,
	String host_nickname,

	Long theme_id,
	String theme_name
) {
	public static PartyDto toDto(Party party) {
		return new PartyDto(
			party.getId(),
			party.getTitle(),
			party.getContent(),
			party.getScheduledAt(),
			party.getParticipantsNeeded(),
			party.getAcceptedParticipantsCount(),
			party.getTotalParticipants(),
			party.getRookieAvailable(),
			party.getStatus(),

			party.getHost().getId(),
			party.getHost().getNickname(),

			party.getTheme().getId(),
			party.getTheme().getName()
		);
	}
}
