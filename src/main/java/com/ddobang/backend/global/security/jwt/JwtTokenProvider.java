package com.ddobang.backend.global.security.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {
	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.exp.access}")
	private Long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	private Key key;

	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateAccessToken(String subject, boolean isAdmin) {
		return buildToken(subject, accessTokenExpiration, isAdmin);
	}

	public String generateRefreshToken(String subject, boolean isAdmin) {
		return buildToken(subject, refreshTokenExpiration, isAdmin);
	}

	// 토큰 생성
	private String buildToken(String subject, long expirationMillis, boolean isAdmin) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMillis);

		return Jwts.builder()
			.setSubject(subject) // 사용자 ID
			.claim("isAdmin", isAdmin) // 관리자 여부
			.setIssuedAt(now) // 발급일
			.setExpiration(expiry) // 만료일
			.signWith(key, SignatureAlgorithm.HS256) // 알고리즘, 키 적용
			.compact(); // 생성
	}
}
