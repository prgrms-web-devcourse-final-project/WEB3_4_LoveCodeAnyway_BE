package com.ddobang.backend.global.util;

import org.springframework.stereotype.Component;

import com.ddobang.backend.global.config.AppConfig;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {

	private final int ACCESS_TOKEN_EXPIRE_SEC = 60 * 60 * 12; // 12시간
	private final int REFRESH_TOKEN_EXPIRE_SEC = 60 * 60 * 24 * 7; // 7일

	private final String ACCESS_TOKEN_KEY = "accessToken";
	private final String REFRESH_TOKEN_KEY = "refreshToken";

	// Access Token 쿠키 생성
	public Cookie createAccessTokenCookie(String token) {
		Cookie cookie = new Cookie(ACCESS_TOKEN_KEY, token);

		commonCookieSetting(cookie);

		cookie.setMaxAge(ACCESS_TOKEN_EXPIRE_SEC);
		return cookie;
	}

	// Refresh Token 쿠키 생성
	public Cookie createRefreshTokenCookie(String token) {
		Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, token);

		commonCookieSetting(cookie);

		cookie.setPath("/");
		cookie.setMaxAge(REFRESH_TOKEN_EXPIRE_SEC);
		return cookie;
	}

	// 회원가입용 토큰 쿠키 생성
	public Cookie createSignupTokenCookie(String token) {
		Cookie cookie = new Cookie("signupToken", token);

		commonCookieSetting(cookie);

		cookie.setPath("/");
		cookie.setMaxAge(60 * 10); // 10분
		return cookie;
	}

	// Access Token 쿠키 값 가져오기
	public String getAccessToken(HttpServletRequest request) {
		if (request.getCookies() == null)
			return null;

		for (Cookie cookie : request.getCookies()) {
			if (ACCESS_TOKEN_KEY.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

	// 쿠키 제거
	public Cookie deleteCookie(String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setHttpOnly(true);
		cookie.setAttribute("SameSite", "None");
		cookie.setSecure(false); // 배포 환경에서 true

		if (AppConfig.isProd())
			cookie.setSecure(true);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		return cookie;
	}

	private void commonCookieSetting(Cookie cookie) {
		cookie.setHttpOnly(true);
		if (AppConfig.isProd()) {
			cookie.setSecure(true);
			cookie.setAttribute("SameSite", "None");
		} else {
			cookie.setSecure(false);
			cookie.setAttribute("SameSite", "Lax");
		}
		cookie.setPath("/");
	}
}
