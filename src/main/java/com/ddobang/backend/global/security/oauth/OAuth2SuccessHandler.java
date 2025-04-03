package com.ddobang.backend.global.security.oauth;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.auth.exception.AuthException;
import com.ddobang.backend.domain.auth.exception.OAuth2ErrorCode;
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
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		log.info("OAuth2 로그인 성공: {}", authentication.getName());

		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

		Object idObj = oAuth2User.getAttribute("id");
		if (idObj == null) {
			log.error("OAuth2User에 'id' 속성이 없습니다. 전체 attributes: {}", oAuth2User.getAttributes());
			throw new AuthException(OAuth2ErrorCode.OAUTH2_MISSING_ID);
		}
		String kakaoId = idObj.toString();

		Map<String, Object> properties = oAuth2User.getAttribute("properties");
		String nickname = null;

		if (properties != null && properties.containsKey("nickname")) {
			nickname = (String)properties.get("nickname");
			log.info("카카오 닉네임: {}", nickname);
		} else {
			log.warn("카카오 응답에 '닉네임'이 없습니다. 기본 닉네임으로 처리합니다.");
		}

		log.info("카카오 ID: {}", kakaoId);

		Member member;
		try {
			member = memberRepository.findByKakaoId(kakaoId)
				.orElseGet(() -> {
					log.info("신규 회원입니다: {}", kakaoId);
					return memberService.createMemberFromOAuth2(oAuth2User); // nickname은 내부에서 처리
				});
		} catch (Exception e) {
			log.error("회원 정보 처리 중 예외 발생", e);
			throw new AuthException(OAuth2ErrorCode.OAUTH2_MEMBER_PROCESS_FAIL);
		}

		boolean isAdmin = member.getPassword() != null;

		try {
			String accessToken = jwtTokenProvider.generateAccessToken(member.getKakaoId(), isAdmin, nickname);
			String refreshToken = jwtTokenProvider.generateRefreshToken(member.getKakaoId(), isAdmin, nickname);

			response.addCookie(CookieUtil.createAccessTokenCookie(accessToken));
			response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken));
		} catch (Exception e) {
			log.error("JWT 토큰 생성 중 예외 발생", e);
			throw new AuthException(OAuth2ErrorCode.OAUTH2_TOKEN_CREATE_FAIL);
		}

		log.info("JWT 토큰 생성 및 쿠키 전송 완료");

		response.sendRedirect("http://localhost:3000");
	}
}
