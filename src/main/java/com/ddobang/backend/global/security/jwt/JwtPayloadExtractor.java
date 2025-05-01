package com.ddobang.backend.global.security.jwt;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

/**
 * JWT 토큰에서 페이로드를 추출하는 클래스
 * JWT 토큰에서 필요한 정보를 추출하는 역할을 수행
 */
@Component
@RequiredArgsConstructor
public class JwtPayloadExtractor {

	private final JwtSigningKey jwtSigningKey;

	// Claims 추출
	private Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(jwtSigningKey.getKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	// 주 식별자 추출 (카카오ID or 닉네임)
	public String extractKakaoId(String token) {
		if (extractTokenType(token) != JwtTokenType.SIGNUP) {
			throw new IllegalStateException("kakaoId는 회원가입 토큰에서만 추출 가능합니다.");
		}
		return extractSubject(token);
	}

	public String extractNickname(String token) {
		if (extractTokenType(token) == JwtTokenType.SIGNUP) {
			throw new IllegalStateException("nickname은 ACCESS/REFRESH 토큰에서만 추출 가능합니다.");
		}
		return extractSubject(token);
	}

	// isAdmin 클레임 추출
	public boolean extractIsAdmin(String token) {
		return Boolean.TRUE.equals(getClaims(token).get("isAdmin", Boolean.class));
	}

	// 토큰 타입 추출 (필수)
	private JwtTokenType extractTokenType(String token) {
		String type = getClaims(token).get("type", String.class);
		return JwtTokenType.valueOf(type);
	}

	// 주 식별자 추출
	public String extractSubject(String token) {
		return getClaims(token).getSubject(); // sub = memberId
	}
}
