package com.ddobang.backend.domain.party.repository;

import com.ddobang.backend.domain.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
}
