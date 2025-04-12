package com.ddobang.backend.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findByKakaoId(String kakaoId);

	boolean existsByKakaoId(String kakaoId);

	Optional<Member> findByNickname(String nickname);

	boolean existsByNickname(String nickname);
}
