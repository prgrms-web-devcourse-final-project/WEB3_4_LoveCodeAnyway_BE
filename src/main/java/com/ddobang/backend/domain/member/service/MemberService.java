package com.ddobang.backend.domain.member.service;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	// OAuth2User 정보로 회원 생성
	public Member createMemberFromOAuth2(OidcUser oidcUser) {
		String sub = oidcUser.getSubject();
		String nickname = oidcUser.getAttribute("nickname");

		return memberRepository.save(Member.builder()
			.kakaoId(sub) // 카카오 ID
			.nickname(nickname) // 닉네임
			.build());
	}

	/**
	 * 사용자명으로 회원 조회
	 */
	public Member getMemberByUsername(String username) {
		// 테스트용 더미 Member 반환
		return Member.builder()
			.id(1L)
			//.username(username)
			.nickname("테스트사용자")
			//.email("test@example.com")
			.build();
	}

	/**
	 * ID로 회원 조회
	 */
	public Member getMemberById(Long id) {
		// 테스트용 더미 Member 반환
		return Member.builder()
			.id(id)
			//.username("user" + id)
			.nickname("사용자" + id)
			//.email("user" + id + "@example.com")
			.build();
	}
}
