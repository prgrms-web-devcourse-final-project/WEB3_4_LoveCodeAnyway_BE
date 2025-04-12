package com.ddobang.backend.global.security.jwt;

import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtTokenFactory tokenFactory;
	private final JwtTokenValidator tokenValidator;
	private final JwtPayloadExtractor payloadExtractor;
	private final JwtCookieResolver cookieResolver;

	//>>> 유효성 검증 그룹
	// JWT 토큰이 유효한지 검증
	public boolean isValidToken(String token, JwtTokenType type) {
		return tokenValidator.isValidToken(token, type);
	}

	//>>> 토큰 추출 그룹
	// JWT 토큰에서 주 식별자 추출
	public String getSubject(String token) {
		return payloadExtractor.extractSubject(token);
	}

	// JWT 토큰에서 카카오ID 추출
	public String extractKakaoId(String token) {
		return payloadExtractor.extractKakaoId(token);
	}

	// JWT 토큰에서 닉네임 추출
	public String extractNickname(String token) {
		return payloadExtractor.extractNickname(token);
	}

	// JWT 토큰에서 권한 추출
	public boolean extractIsAdmin(String token) {
		return payloadExtractor.extractIsAdmin(token);
	}

	// 쿠키에서 액세스 토큰 추출
	public String resolveAccessToken(HttpServletRequest request) {
		return cookieResolver.resolveAccessToken(request);
	}

	//>>> 토큰 생성 그룹
	public String generateToken(Member member, JwtTokenType type, boolean isAdmin) {
		return tokenFactory.generateToken(member, type, isAdmin);
	}

	public String generateSignupToken(String kakaoId) {
		return tokenFactory.generateSignupToken(kakaoId);
	}
}
