package com.ddobang.backend.global.security.jwt.dto;

public record TokenResponse(
	String token, // 토큰 값
	TokenType tokenType, // 토큰 타입
	long accessTokenExpiresIn // 엑세스 토큰 만료 시간
) {
	public static TokenResponse of(String token, TokenType tokenType, long expiresIn) {
		return new TokenResponse(token, tokenType, expiresIn);
	}
}
