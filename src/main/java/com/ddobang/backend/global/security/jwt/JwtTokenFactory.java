package com.ddobang.backend.global.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.exception.MemberErrorCode;
import com.ddobang.backend.domain.member.exception.MemberException;
import com.ddobang.backend.domain.member.service.MemberService;

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
	private final MemberService memberService;

	// JWT 토큰을 생성하는 메서드
	public String generateToken(String kakaoId, JwtTokenType type, boolean isAdmin) {
		Member member = memberService.findByKakaoId(kakaoId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

		long expiration = jwtTokenProperties.getExpiration(type);
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setSubject(String.valueOf(member.getId()))
			.setIssuedAt(now)
			.setExpiration(expiry)
			.claim("nickname", member.getNickname())
			.claim("type", type.name())
			.claim("isAdmin", isAdmin)
			.signWith(jwtSigningKey.getKey(), SignatureAlgorithm.HS256)
			.compact();
	}
}
