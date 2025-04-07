package com.ddobang.backend.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {

	private static final int ACCESS_TOKEN_EXPIRE_SEC = 60 * 30; // 30분
	private static final int REFRESH_TOKEN_EXPIRE_SEC = 60 * 60 * 24; // 1일

	private static final String ACCESS_TOKEN_KEY = "accessToken";
	private static final String REFRESH_TOKEN_KEY = "refreshToken";

	// Access Token 쿠키 생성
	public static Cookie createAccessTokenCookie(String token) {
		Cookie cookie = new Cookie(ACCESS_TOKEN_KEY, token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false); // 배포 환경에서 true
		cookie.setPath("/");
		cookie.setMaxAge(ACCESS_TOKEN_EXPIRE_SEC);
		return cookie;
	}

	// Refresh Token 쿠키 생성
	public static Cookie createRefreshTokenCookie(String token) {
		Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false); // 배포 환경에서 true
		cookie.setPath("/");
		cookie.setMaxAge(REFRESH_TOKEN_EXPIRE_SEC);
		return cookie;
	}

	// Access Token 쿠키 값 가져오기
	public static String getAccessToken(HttpServletRequest request) {
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
	public static Cookie deleteCookie(String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		return cookie;
	}
}
