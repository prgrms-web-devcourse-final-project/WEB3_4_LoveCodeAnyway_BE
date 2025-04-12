package com.ddobang.backend.global.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;

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

	// 회원가입용: kakaoId 기반
	public String generateSignupToken(String kakaoId) {
		return createToken(kakaoId, JwtTokenType.SIGNUP, false, null);
	}

	// 로그인/인증용: member 기반
	public String generateToken(Member member, JwtTokenType type, boolean isAdmin) {
		return createToken(String.valueOf(member.getId()), type, isAdmin, member.getNickname());
	}

	private String createToken(String subject, JwtTokenType type, boolean isAdmin, String nickname) {
		long expiration = jwtTokenProperties.getExpiration(type);
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setSubject(subject)
			.setIssuedAt(now)
			.setExpiration(expiry)
			.claim("type", type.name())
			.claim("isAdmin", isAdmin)
			.claim("nickname", nickname)
			.signWith(jwtSigningKey.getKey(), SignatureAlgorithm.HS256)
			.compact();
	}
}
