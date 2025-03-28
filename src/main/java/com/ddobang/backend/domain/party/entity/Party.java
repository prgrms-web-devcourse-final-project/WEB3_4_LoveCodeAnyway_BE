package com.ddobang.backend.domain.party.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.exception.PartyErrorCode;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Party extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotBlank
	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@NotBlank
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	@NotNull
	@FutureOrPresent
	@Column(name = "scheduled_at", nullable = false)
	private LocalDateTime scheduledAt;

	@Builder.Default
	@NotNull
	@Column(name = "participants_needed", nullable = false)
	private Integer participantsNeeded = 1;

	@NotNull
	@Column(name = "total_participants", nullable = false)
	private Integer totalParticipants;

	@NotNull
	@Column(name = "rookie_available", nullable = false)
	private Boolean rookieAvailable;

	@Builder.Default
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PartyStatus status = PartyStatus.RECRUITING; // RECRUITING, CLOSED, COMPLETED, CANCELLED

	@Builder.Default
	@NotNull
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@Builder.Default
	@OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PartyMember> partyMembers = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "theme_id", nullable = false)
	private Theme theme;

	@PrePersist
	@PreUpdate
	private void validateParticipants() {
		if (participantsNeeded != null && totalParticipants != null) {
			if (participantsNeeded > totalParticipants) {
				throw new PartyException(PartyErrorCode.PARTY_INVALID_PARTICIPANTS);
			}
		}
	}

	public Member getHost() {
		return partyMembers.stream()
			.filter(pm -> pm.getStatus() == PartyMemberStatus.HOST)
			.findFirst()
			.map(PartyMember::getMember)
			.orElseThrow(() -> new PartyException(PartyErrorCode.PARTY_HOST_NOT_FOUND));
	}

	public List<PartyMember> getApplicants() {
		return partyMembers.stream()
			.filter(pm -> pm.getStatus() == PartyMemberStatus.APPLICANT)
			.collect(Collectors.toList());
	}

	public List<PartyMember> getAcceptedMembers() {
		return partyMembers.stream()
			.filter(pm -> pm.getStatus() == PartyMemberStatus.ACCEPTED)
			.collect(Collectors.toList());
	}
}
