package com.ddobang.backend.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.member.entity.MemberStat;

@Repository
public interface MemberStatRepository extends JpaRepository<MemberStat, Long> {
	@Modifying
	@Query(value = "UPDATE member_stat "
		+ "SET days_since_first_escape = days_since_first_escape + 1 "
		+ "WHERE first_escape_date IS NOT NULL",
		nativeQuery = true
	)
	int incrementDaysSinceFirstEscape();

	Optional<MemberStat> findByMemberId(Long memberId); // 회원 ID로 통계 조회
}
