package com.ddobang.backend.global.security.jwt;

import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

	private final JwtSigningKey signingKey;

	// 토큰이 주어진 타입(ACCESS, REFRESH 등)에 대해 유효한지 검사
	public boolean isValidToken(String token, JwtTokenType tokenType) {
		try {
			Claims claims = parseClaims(token);
			String type = claims.get("type", String.class);

			// 토큰 타입이 일치하는지 확인
			if (!tokenType.name().equals(type)) {
				throw new JwtException("잘못된 토큰 타입: 기대값 = " + tokenType.name() + ", 실제값 = " + type);
			}
			return true;

		} catch (JwtException | IllegalArgumentException e) {
			// 유효하지 않음
			return false;
		}
	}

	// Claims를 추출 (만료/서명 검증 포함)
	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(signingKey.getKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
