package com.ddobang.backend.domain.party;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

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
