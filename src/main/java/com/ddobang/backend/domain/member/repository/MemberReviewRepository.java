package com.ddobang.backend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.member.entity.MemberReview;

@Repository
public interface MemberReviewRepository extends JpaRepository<MemberReview, Long> {
}
