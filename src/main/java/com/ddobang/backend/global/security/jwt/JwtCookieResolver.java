package com.ddobang.backend.global.security.jwt;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtCookieResolver {

	private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
	private static final String SIGNUP_TOKEN_COOKIE_NAME = "signupToken";

	// 액세스 토큰 추출
	public String resolveAccessToken(HttpServletRequest request) {
		return extractTokenFromCookie(request, ACCESS_TOKEN_COOKIE_NAME);
	}

	// 회원가입용 토큰 추출 (필요 시)
	public String resolveSignupToken(HttpServletRequest request) {
		return extractTokenFromCookie(request, SIGNUP_TOKEN_COOKIE_NAME);
	}

	// 공통 쿠키 추출 로직
	private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
		if (request.getCookies() == null)
			return null;

		for (var cookie : request.getCookies()) {
			if (cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
