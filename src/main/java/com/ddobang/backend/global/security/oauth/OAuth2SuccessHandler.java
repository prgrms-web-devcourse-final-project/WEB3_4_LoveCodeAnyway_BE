package com.ddobang.backend.global.security.oauth;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private final MemberRepository memberRepository;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException { // OAuth2 로그인 성공 시 처리될 내용
		log.info("OAuth2 로그인 성공: {}", authentication.getName());

		// 사용자 정보 추출 (카카오 ID 기준)
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		String kakaoId = String.valueOf(oAuth2User.getAttribute("id")); // or "sub"

		log.info("카카오 ID: {}", kakaoId);

		// 기존 회원 여부 확인
		Optional<Member> existMember = memberRepository.findByOauthId(kakaoId);

		if (existMember.isPresent()) {
			log.info("기존 회원: {}", kakaoId);
		} else {
			log.info("신규 회원입니다: {}", kakaoId);
			// 신규회원 가입 처리 로직
		}
	}
}
