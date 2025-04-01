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
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.util.CookieUtil;

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
	private final JwtTokenProvider jwtTokenProvider;

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

		Member member;

		// 기존 회원이면 로그인 처리
		if (existMember.isPresent()) {
			log.info("기존 회원: {}", kakaoId);
			member = existMember.get(); // 회원 정보 가져오기
		} else {
			// 신규 회원이면 회원 생성
			log.info("신규 회원입니다: {}", kakaoId);
			member = memberService.createMemberFromOAuth2(oidcUser); // 신규 회원 정보 가져오기
		}

		boolean isAdmin = member.getPassword() != null; // 비밀번호가 존재하면 관리자로 판단

		String accessToken = jwtTokenProvider.generateAccessToken(member.getKakaoId(), isAdmin);
		String refreshToken = jwtTokenProvider.generateRefreshToken(member.getKakaoId(), isAdmin);

		response.addCookie(CookieUtil.createAccessTokenCookie(accessToken));
		response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken));

		log.info("JWT 토큰 생성 및 쿠키 전송 완료");
	}
}
