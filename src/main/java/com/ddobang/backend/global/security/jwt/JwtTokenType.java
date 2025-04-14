package com.ddobang.backend.global.security.jwt;

/**
 * JWT 토큰의 종류를 정의하는 enum 클래스입니다.
 * - SIGNUP: 회원가입용 토큰
 * - ACCESS: 엑세스 토큰
 * - REFRESH: 리프레시 토큰
 */
public enum JwtTokenType {
	SIGNUP,
	ACCESS,
	REFRESH
}
