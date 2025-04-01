package com.ddobang.backend.domain.party.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.domain.party.exception.PartyErrorCode;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.types.PartyMemberRole;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Party extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@NotNull
	@Column(name = "participants_needed", nullable = false)
	private Integer participantsNeeded = 1;

	@NotNull
	@Column(name = "total_participants", nullable = false)
	private Integer totalParticipants;

	@NotNull
	@Column(name = "rookie_available", nullable = false)
	private Boolean rookieAvailable;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PartyStatus status; // RECRUITING, CLOSED, COMPLETED, CANCELLED

	@NotNull
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PartyMember> partyMembers;

	@ManyToOne
	@JoinColumn(name = "theme_id", nullable = false)
	private Theme theme;

	private Party(PartyRequest request, Theme theme, Member host) {
		this.title = request.title();
		this.content = request.content();
		this.scheduledAt = request.scheduledAt();
		this.participantsNeeded = request.participantsNeeded();
		this.totalParticipants = request.totalParticipants();
		this.rookieAvailable = request.rookieAvailable();
		this.status = PartyStatus.RECRUITING;
		this.partyMembers = new ArrayList<>();
		this.isDeleted = false;
		this.theme = theme;

		PartyMember partyMember = PartyMember.createHost(this, host);
		this.partyMembers.add(partyMember);
	}

	public static Party of(PartyRequest request, Theme theme, Member host) {
		return new Party(request, theme, host);
	}

	public void modifyParty(PartyRequest request, Theme theme) {
		this.title = request.title();
		this.content = request.content();
		this.scheduledAt = request.scheduledAt();
		this.participantsNeeded = request.participantsNeeded();
		this.totalParticipants = request.totalParticipants();
		this.rookieAvailable = request.rookieAvailable();
		this.theme = theme;
	}

	public boolean isPartyMember(Member member) {
		return partyMembers.stream()
			.anyMatch(pm -> pm.getMember().equals(member));
	}

	public boolean isRecruiting() {
		if (this.getStatus() != PartyStatus.RECRUITING) {
			return false;
		}
		return true;
	}

	public boolean isOpen() {
		return this.status == PartyStatus.RECRUITING || this.status == PartyStatus.FULL;
	}

	public void updatePartyStatus() {
		long acceptedCount = getAcceptedMembers().size();

		if (this.status == PartyStatus.COMPLETED || this.status == PartyStatus.CANCELLED) {
			return;
		}

		if (LocalDateTime.now().isAfter(scheduledAt)) {
			this.status = PartyStatus.PENDING;
			return;
		}

		if (acceptedCount >= participantsNeeded) {
			this.status = PartyStatus.FULL;
		} else {
			this.status = PartyStatus.RECRUITING;
		}
	}

	public void updateFinalStatus(PartyStatus status) {
		this.status = status;
	}

	public void addPartyMember(Member member) {
		PartyMember partyMember = PartyMember.of(this, member);
		this.partyMembers.add(partyMember);
	}

	private PartyMember getPartyMember(Member member) {
		return partyMembers.stream()
			.filter(pm -> pm.getMember().equals(member))
			.findFirst()
			.orElseThrow(() -> new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_FOUND));
	}

	public PartyMemberRole getPartyMemberRole(Member member) {
		PartyMember partyMember = getPartyMember(member);
		return partyMember.getRole();
	}

	public PartyMemberStatus getPartyMemberStatus(Member member) {
		PartyMember partyMember = getPartyMember(member);
		return partyMember.getStatus();
	}

	public void updatePartyMemberStatus(Member member, PartyMemberStatus status) {
		PartyMember partyMember = getPartyMember(member);
		partyMember.changeStatus(status);
	}

	public void delete() {
		if (isDeleted) {
			throw new PartyException(PartyErrorCode.PARTY_ALREADY_DELETED);
		}
		this.isDeleted = true;
	}

	public Member getHost() {
		return partyMembers.stream()
			.filter(pm -> pm.getRole() == PartyMemberRole.HOST)
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

	@PrePersist
	@PreUpdate
	private void validateParticipants() {
		if (participantsNeeded != null && totalParticipants != null) {
			if (participantsNeeded > totalParticipants) {
				throw new PartyException(PartyErrorCode.PARTY_INVALID_PARTICIPANTS);
			}
		}
	}
}
