package com.ddobang.backend.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.exception.MemberErrorCode;
import com.ddobang.backend.domain.member.exception.MemberException;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.global.exception.jwt.JwtErrorCode;
import com.ddobang.backend.global.exception.jwt.JwtException;

import lombok.RequiredArgsConstructor;

/**
 * 현재 로그인한 사용자의 정보를 제공하는 클래스입니다.
 * @author Jay Lim
 */
@Component
@RequiredArgsConstructor
public class LoginMemberProvider {

	private final MemberRepository memberRepository;

	public Member getCurrentMember() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()
			|| !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
			throw new JwtException(JwtErrorCode.INVALID_PRINCIPAL);
		}

		// 로그인한 사용자의 ID로 회원 객체를 조회
		return memberRepository.findById(userDetails.memberId())
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
	}
}
