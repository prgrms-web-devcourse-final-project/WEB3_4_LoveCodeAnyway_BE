package com.ddobang.backend.domain.party.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.party.entity.PartyMemberReview;

@Repository
public interface PartyMemberReviewRepository extends JpaRepository<PartyMemberReview, Long> {
	List<PartyMemberReview> findByReceiverId(Long receiverId);

}
