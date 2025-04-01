package com.ddobang.backend.global.security.oauth;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.member.service.MemberService;

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
	private final MemberService memberService;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException { // OAuth2 로그인 성공 시 처리될 내용
		log.info("OAuth2 로그인 성공: {}", authentication.getName());

		// 카카오 ID 추출
		OidcUser oidcUser = (OidcUser)authentication.getPrincipal();
		String kakaoId = oidcUser.getSubject(); //
		log.info("카카오 ID: {}", kakaoId);

		// 기존 회원 여부 확인
		Optional<Member> existMember = memberRepository.findByKakaoId(kakaoId);

		// 기존 회원이면 로그인 처리
		if (existMember.isPresent()) {
			log.info("기존 회원: {}", kakaoId);
		} else {
			// 신규 회원이면 회원 생성
			log.info("신규 회원입니다: {}", kakaoId);
			memberService.createMemberFromOAuth2(oidcUser);
		}
	}
}
