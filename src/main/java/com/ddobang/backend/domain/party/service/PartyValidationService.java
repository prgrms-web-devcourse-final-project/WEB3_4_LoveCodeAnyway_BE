package com.ddobang.backend.domain.party.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.exception.PartyErrorCode;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.types.PartyMemberRole;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;

@Service
public class PartyValidationService {
	public void checkHost(Party party, Member member) {
		if (!party.getPartyMemberRole(member).equals(PartyMemberRole.HOST)) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_HOST);
		}
	}

	public void validateApply(Party party, Member member) {
		if (!party.isPartyMember(member)) {
			return;
		}
		if (party.getPartyMemberRole(member) == PartyMemberRole.HOST
			|| party.getPartyMemberStatus(member) == PartyMemberStatus.ACCEPTED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_APPLICANT);
		}
	}

	public void validateCancel(Party party, Member member) {
		if (!party.isPartyMember(member) || party.getPartyMemberStatus(member) == PartyMemberStatus.CANCELLED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_FOUND);
		}
		if (party.getPartyMemberRole(member) == PartyMemberRole.HOST) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_CANNOT_CANCEL_HOST);
		}
	}

	public void validateAccept(Party party, Member member) {
		if (!party.isPartyMember(member) || party.getPartyMemberStatus(member) == PartyMemberStatus.CANCELLED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_FOUND);
		}
		if (party.getPartyMemberRole(member) == PartyMemberRole.HOST
			|| party.getPartyMemberStatus(member) == PartyMemberStatus.ACCEPTED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_APPLICANT);
		}
	}

	public void checkRecruiting(Party party) {
		if (!party.isRecruiting()) {
			throw new PartyException(PartyErrorCode.PARTY_NOT_REQUITING);
		}
	}

	public void checkOpen(Party party) {
		if (!party.isOpen()) {
			throw new PartyException(PartyErrorCode.PARTY_NOT_OPEN);
		}
	}

	public void checkExecutable(Party party) {
		boolean afterScheduledTime = LocalDateTime.now().isAfter(party.getScheduledAt());
		boolean isPending = party.getStatus() == PartyStatus.PENDING;

		if (!afterScheduledTime && !isPending) {
			throw new PartyException(PartyErrorCode.PARTY_NOT_EXECUTABLE);
		}
	}
}
