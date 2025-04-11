package com.ddobang.backend.global.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * JWT 관련 설정을 담고 있는 클래스입니다.
 * JWT 토큰의 만료 시간과 같은 설정을 관리합니다.
 */
@Component
@Getter
public class JwtTokenProperties {

	@Value("${jwt.signup-token-expiration}")
	private long signupTokenExpiration;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	// JWT 토큰별 만료 시간을 가져오는 메서드
	public long getExpiration(JwtTokenType type) {
		return switch (type) {
			case SIGNUP -> signupTokenExpiration;
			case ACCESS -> accessTokenExpiration;
			case REFRESH -> refreshTokenExpiration;
		};
	}
}
