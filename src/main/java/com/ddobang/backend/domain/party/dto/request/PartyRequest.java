package com.ddobang.backend.domain.party.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.ddobang.backend.global.validation.annotation.ValidParticipants;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidParticipants
public record PartyRequest(
	@NotNull
	Long themeId,

	@NotBlank
	@Size(max = 100)
	String title,

	@NotBlank
	@Size(max = 1000)
	String content,

	@NotNull
	@FutureOrPresent
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	LocalDateTime scheduledAt,

	@NotNull
	@Min(1)
	Integer participantsNeeded,

	@NotNull
	@Min(1)
	Integer totalParticipants,

	@NotNull
	Boolean rookieAvailable
) {
}
