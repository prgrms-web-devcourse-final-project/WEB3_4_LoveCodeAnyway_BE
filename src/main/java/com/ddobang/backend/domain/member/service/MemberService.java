package com.ddobang.backend.domain.member.service;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	// OAuth2User 정보로 회원 생성
	public Member createMemberFromOAuth2(OAuth2User oAuth2User) {
		String kakaoId = oAuth2User.getAttribute("id").toString();

		Map<String, Object> properties = oAuth2User.getAttribute("properties");
		String nickname = null;

		if (properties != null && properties.containsKey("nickname")) {
			nickname = (String)properties.get("nickname");
		}

		// 닉네임 없으면 기본값 사용
		if (nickname == null || nickname.isBlank()) {
			nickname = "기본닉네임";
		}

		Member member = Member.builder()
			.kakaoId(kakaoId)
			.nickname(nickname)
			.build();

		return memberRepository.save(member);
	}

	/**
	 * 사용자명으로 회원 조회
	 */
	public Member getMemberByUsername(String username) {
		// 테스트용 더미 Member 반환
		return Member.builder()
			// .id(1L)
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
			// .id(id)
			//.username("user" + id)
			.nickname("사용자" + id)
			//.email("user" + id + "@example.com")
			.build();
	}
}
