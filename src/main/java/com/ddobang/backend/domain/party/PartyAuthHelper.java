package com.ddobang.backend.domain.party;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

// TODO: 임시 인증 헬퍼 클래스입니다. 멤버 도메인 쪽 인증 처리 완성되면 삭제 예정입니다
@Component
@RequiredArgsConstructor
public class PartyAuthHelper {

	private final MemberRepository memberRepository;

	public Member getCurrentMember() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("No authenticated user found.");
		}

		Long memberId = (Long)authentication.getPrincipal();
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new RuntimeException("Member not found"));
	}
}
