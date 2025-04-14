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
	public void checkPartyMember(Party party, Member member) {
		if (!party.isPartyMember(member)) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_FOUND);
		}
	}

	public void checkHost(Party party, Member member) {
		if (!party.getPartyMemberRole(member).equals(PartyMemberRole.HOST)) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_HOST);
		}
	}

	public void checkRecruiting(Party party) {
		if (!party.isRecruiting()) {
			throw new PartyException(PartyErrorCode.PARTY_NOT_REQUITING);
		}
	}

	public void validateApply(Party party, Member member) {
		checkRecruiting(party);

		if (!party.isPartyMember(member)) {
			return;
		}

		PartyMemberRole role = party.getPartyMemberRole(member);
		PartyMemberStatus status = party.getPartyMemberStatus(member);

		if (role == PartyMemberRole.HOST || status == PartyMemberStatus.ACCEPTED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_ALREADY_ACCEPTED);
		}
	}

	public void validateCancel(Party party, Member member) {
		checkRecruiting(party);
		checkPartyMember(party, member);

		PartyMemberRole role = party.getPartyMemberRole(member);
		PartyMemberStatus status = party.getPartyMemberStatus(member);

		if (status == PartyMemberStatus.CANCELLED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_FOUND);
		}
		if (role == PartyMemberRole.HOST) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_CANNOT_CANCEL_HOST);
		}
	}

	public void validateAccept(Party party, Member member, Member actor) {
		checkRecruiting(party);
		checkHost(party, actor);
		checkPartyMember(party, member);

		PartyMemberRole role = party.getPartyMemberRole(member);
		PartyMemberStatus status = party.getPartyMemberStatus(member);

		if (status == PartyMemberStatus.CANCELLED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_FOUND);
		}
		if (role == PartyMemberRole.HOST
			|| status == PartyMemberStatus.ACCEPTED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_ALREADY_ACCEPTED);
		}
	}

	public void validateReject(Party party, Member member, Member actor) {
		checkRecruiting(party);
		checkHost(party, actor);
		checkPartyMember(party, member);

		PartyMemberRole role = party.getPartyMemberRole(member);
		PartyMemberStatus status = party.getPartyMemberStatus(member);

		if (status == PartyMemberStatus.CANCELLED) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_FOUND);
		}

		if (role == PartyMemberRole.HOST) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_CANNOT_CANCEL_HOST);
		}

		if (status != PartyMemberStatus.APPLICANT) {
			throw new PartyException(PartyErrorCode.PARTY_MEMBER_NOT_APPLICANT);
		}
	}

	public void validateExecutable(Party party, Member actor) {

		checkHost(party, actor);

		boolean afterScheduledTime = LocalDateTime.now().isAfter(party.getScheduledAt());
		boolean isPending = party.getStatus() == PartyStatus.PENDING;

		if (!afterScheduledTime && !isPending) {
			throw new PartyException(PartyErrorCode.PARTY_NOT_EXECUTABLE);
		}
	}

	public void validateModifiable(Party party, Member member) {
		checkHost(party, member);

		if ((long)party.getPartyMembers().size() > 1) {
			throw new PartyException(PartyErrorCode.PARTY_NOT_MODIFIABLE);
		}
	}
}
