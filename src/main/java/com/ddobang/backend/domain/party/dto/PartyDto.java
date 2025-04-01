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
	Integer totalParticipants,
	Boolean rookieAvailable,
	PartyStatus status
	// MemberDto host,
	// ThemeDto theme
) {
	public static PartyDto toDto(Party party) {
		return new PartyDto(
			party.getId(),
			party.getTitle(),
			party.getContent(),
			party.getScheduledAt(),
			party.getParticipantsNeeded(),
			party.getTotalParticipants(),
			party.getRookieAvailable(),
			party.getStatus()
		);
	}
}
