package com.ddobang.backend.domain.party.entity;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class PartyMember {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "party_id", nullable = false)
	private Party party;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PartyMemberStatus status; // HOST, APPLICANT. ACCEPTED, REJECTED, CANCELLED

	private PartyMember(Party party, Member member) {
		this.party = party;
		this.member = member;
		this.status = PartyMemberStatus.APPLICANT;
	}

	public static PartyMember of(Party party, Member member) {
		return new PartyMember(party, member);
	}

	public void changeStatus(PartyMemberStatus status) {
		this.status = status;
	}
}
