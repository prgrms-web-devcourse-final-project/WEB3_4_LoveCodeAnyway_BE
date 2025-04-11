package com.ddobang.backend.global.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

/**
 * JWT 토큰을 생성하는 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFactory {

	private final JwtTokenProperties jwtTokenProperties;
	private final JwtSigningKey jwtSigningKey;

	// JWT 토큰을 생성하는 메서드
	public String generateToken(String subject, JwtTokenType type, boolean isAdmin) {
		long expiration = jwtTokenProperties.getExpiration(type);
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setSubject(subject)
			.setIssuedAt(now)
			.setExpiration(expiry)
			.claim("type", type.name())
			.claim("isAdmin", isAdmin)
			.signWith(jwtSigningKey.getKey(), SignatureAlgorithm.HS256)
			.compact();
	}
}
