package com.ddobang.backend.global.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.access-token-expiration}")
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

	// 토큰 유효성 검사
	public String getSubject(String token) {
		return parseClaims(token).getBody().getSubject();
	}

	public Claims getClaims(String token) {
		return parseClaims(token).getBody();
	}

	// 토큰 파싱
	private Jws<Claims> parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
	}

	// 토큰 유효성 검사 (예외 처리)
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) { // JWT 예외ㅣ잘못된 인자 예외 처리
			return false;
		}
	}

	// 요청 헤더에서 엑세스토큰 추출
	public String resolveAccessToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7); // "Bearer " 이후 토큰만 추출
		}
		return null;
	}

	// 관리자 여부 추출
	public boolean getIsAdmin(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
		return Boolean.TRUE.equals(claims.get("isAdmin", Boolean.class));
	}

	// 추출된 관리자 여부에 따라 권한 설정
	public List<GrantedAuthority> getAuthorities(boolean isAdmin) {
		if (isAdmin) {
			return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
		} else {
			return List.of(new SimpleGrantedAuthority("ROLE_USER"));
		}
	}
}
