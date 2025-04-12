package com.ddobang.backend.domain.party.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.types.PartyStatus;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long>, PartyRepositoryCustom {
	List<Party> findTop12ByStatusOrderByScheduledAtAsc(PartyStatus status);

	List<Party> findByScheduledAtBetweenAndStatusIn(LocalDateTime from, LocalDateTime to, List<PartyStatus> statuses);
}
