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
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.util.CookieUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * OAuth2 로그인 성공 시 호출되는 메서드
	 * @param request
	 * @param response
	 * @param authentication 인증 정보
	 */
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
			log.warn("카카오 응답에 '닉네임'이 없습니다.");
		}

		log.info("카카오 ID: {}", kakaoId);
		log.info("카카오 닉네임: {}", nickname);

		Member member;
		try {
			member = memberService.findByKakaoId(kakaoId)
				.orElseGet(() -> {
					log.info("신규 회원입니다: {}", kakaoId);
					return memberService.createMemberFromOAuth2(oAuth2User); // nickname은 내부에서 처리
				});
		} catch (Exception e) {
			log.error("회원 정보 처리 중 예외 발생", e);
			throw new AuthException(OAuth2ErrorCode.OAUTH2_MEMBER_PROCESS_FAIL);
		}

		boolean isAdmin = member.getPassword() != null;
		// 기존 회원 여부 확인
		if (memberService.existsByKakaoId(kakaoId)) {
			log.info("기존 회원입니다: {}", kakaoId);
			Member member = memberService.findByKakaoId(kakaoId).orElseThrow();
			boolean isAdmin = member.getPassword() != null; // 비밀번호가 있으면 관리자

			try {
				String accessToken = jwtTokenProvider.generateAccessToken(member.getNickname(), isAdmin);
				String refreshToken = jwtTokenProvider.generateRefreshToken(member.getNickname(), isAdmin);

				response.addCookie(CookieUtil.createAccessTokenCookie(accessToken));
				response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken));
			} catch (Exception e) {
				log.error("JWT 토큰 생성 중 예외 발생", e);
				throw new AuthException(OAuth2ErrorCode.OAUTH2_TOKEN_CREATE_FAIL);
			}
			log.info("JWT 토큰 생성 및 쿠키 전송 완료");

			response.sendRedirect("http://localhost:3000"); // 메인 페이지로 리다이렉트
		} else {
			log.info("신규 회원입니다: {}", kakaoId);

			if (nickname == null || nickname.isBlank()) { // 닉네임이 없으면 비워서 전송
				nickname = "";
			}

			String signupToken = jwtTokenProvider.generateSignupToken(kakaoId, nickname); // 회원가입 전용 토큰 생성
			response.addCookie(CookieUtil.createSignupTokenCookie(signupToken)); // 쿠키에 저장
			response.sendRedirect("http://localhost:3000/signup"); // 프로필 등록 페이지로 리다이렉트
		}
	}
}
