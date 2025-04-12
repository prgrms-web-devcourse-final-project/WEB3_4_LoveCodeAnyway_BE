package com.ddobang.backend.domain.party.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.entity.PartyMember;
import com.ddobang.backend.domain.party.repository.PartyRepository;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyScheduler {

	private final PartyRepository partyRepository;

	@Scheduled(fixedRate = 300000) // 5분마다 실행
	@Transactional
	public void updatePartiesToPending() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime tenMinutesLater = now.plusMinutes(10);

		List<Party> parties = partyRepository.findByScheduledAtBetweenAndStatusIn(
			now, tenMinutesLater,
			List.of(PartyStatus.RECRUITING, PartyStatus.FULL)
		);

		for (Party party : parties) {
			party.updateStatus(PartyStatus.PENDING);

			List<PartyMember> applicants = party.getApplicants();
			for (PartyMember pm : applicants) {
				pm.changeStatus(PartyMemberStatus.REJECTED);
			}
		}
	}
}
