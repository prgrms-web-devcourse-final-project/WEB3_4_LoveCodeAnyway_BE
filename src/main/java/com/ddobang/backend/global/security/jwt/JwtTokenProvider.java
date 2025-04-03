package com.ddobang.backend.global.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ddobang.backend.global.exception.JwtErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
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

	// 애플리케이션 실행 시 초기화
	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	// 엑세스 토큰, 리프레시 토큰 생성
	public String generateAccessToken(String nickname, boolean isAdmin) {
		return buildToken(accessTokenExpiration, nickname, isAdmin);
	}

	public String generateRefreshToken(String nickname, boolean isAdmin) {
		return buildToken(refreshTokenExpiration, nickname, isAdmin);
	}

	private String buildToken(long expirationMillis, String nickname, boolean isAdmin) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMillis);

		return Jwts.builder()
			.claim("nickname", nickname) // 닉네임
			.claim("isAdmin", isAdmin) // 관리자 여부
			.setIssuedAt(now) // 발급일
			.setExpiration(expiry) // 만료일
			.signWith(key, SignatureAlgorithm.HS256) // 알고리즘, 키 적용
			.compact(); // 생성
	}

	// 회원가입 전용 토큰 생성
	public String generateSignupToken(String kakaoId, String nickname) {
		return Jwts.builder()
			.claim("kakaoId", kakaoId)
			.claim("nickname", nickname)
			.setIssuedAt(new Date())
			.setExpiration(Date.from(Instant.now().plusSeconds(300))) // 5분 유효
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public Claims getClaims(String token) {
		return parseClaims(token).getBody();
	}

	private Jws<Claims> parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
	}

	// 토큰 유효성 검사
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new ServiceException(JwtErrorCode.TOKEN_EXPIRED); // 만료된 토큰
		} catch (UnsupportedJwtException e) {
			throw new ServiceException(JwtErrorCode.UNSUPPORTED_TOKEN); // 지원하지 않는 토큰
		} catch (MalformedJwtException | SecurityException e) {
			throw new ServiceException(JwtErrorCode.TOKEN_INVALID); // 잘못된 토큰
		} catch (IllegalArgumentException e) {
			throw new ServiceException(JwtErrorCode.TOKEN_MISSING); // 누락된 토큰
		}
	}

	// 엑세스토큰 추출
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

	// 추출된 관리자 여부에 따라 권한 설정 주입
	public List<GrantedAuthority> getAuthorities(boolean isAdmin) {
		if (isAdmin) {
			return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
		} else {
			return List.of(new SimpleGrantedAuthority("ROLE_USER"));
		}
	}
}
