package com.ddobang.backend.global.security.oauth;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
	) throws IOException, ServletException {
		log.info("OAuth2 로그인 성공: {}", authentication.getName());

		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

		String kakaoId = oAuth2User.getAttribute("id").toString();
		Map<String, Object> properties = oAuth2User.getAttribute("properties");
		String nickname = (String)properties.get("nickname");

		log.info("카카오 ID: {}", kakaoId);
		log.info("닉네임: {}", nickname);

		Optional<Member> existMember = memberRepository.findByKakaoId(kakaoId);
		Member member;

		if (existMember.isPresent()) {
			log.info("기존 회원: {}", kakaoId);
			member = existMember.get();
		} else {
			log.info("신규 회원입니다: {}", kakaoId);
			member = memberService.createMemberFromOAuth2(oAuth2User);
		}

		boolean isAdmin = member.getPassword() != null;

		String accessToken = jwtTokenProvider.generateAccessToken(member.getKakaoId(), isAdmin);
		String refreshToken = jwtTokenProvider.generateRefreshToken(member.getKakaoId(), isAdmin);

		response.addCookie(CookieUtil.createAccessTokenCookie(accessToken));
		response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken));

		log.info("JWT 토큰 생성 및 쿠키 전송 완료");

		response.sendRedirect("http://localhost:3000"); // 프론트 페이지로 리다이렉트
	}
}
