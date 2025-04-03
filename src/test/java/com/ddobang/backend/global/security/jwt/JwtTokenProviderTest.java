package com.ddobang.backend.global.security.jwt;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

	private JwtTokenProvider jwtTokenProvider;

	@BeforeEach
	void setUp() {
		jwtTokenProvider = new JwtTokenProvider();

		// 테스트용 수동 주입
		ReflectionTestUtils.setField(jwtTokenProvider, "secret", "testtesttesttesttesttesttesttest");
		ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", 2000L); // 2초
		ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", 10000L); // 10초
		jwtTokenProvider.init();
	}

	@DisplayName("Access Token 발급 후 Subject 추출이 가능해야 한다.")
	@Test
	void generateAccessToken_and_getSubject() {
		String token = jwtTokenProvider.generateAccessToken("ddobang", false, "도방유저");

		String subject = jwtTokenProvider.getSubject(token);

		assertThat(subject).isEqualTo("ddobang");
	}

	@DisplayName("Claims에 isAdmin 값이 포함되어야 한다.")
	@Test
	void getClaims_shouldContainIsAdmin() {
		String token = jwtTokenProvider.generateAccessToken("ddobangAdmin", true, "관리자");

		boolean isAdmin = (boolean)jwtTokenProvider.getClaims(token).get("isAdmin");

		assertThat(isAdmin).isTrue();
	}

	@DisplayName("만료된 토큰은 유효하지 않아야 한다.")
	@Test
	void validateToken_shouldReturnFalse_whenTokenIsExpired() throws InterruptedException {
		String token = jwtTokenProvider.generateAccessToken("ddobang", false, "도방유저");

		TimeUnit.MILLISECONDS.sleep(3000); // 3초 대기해서 만료 유도

		boolean isValid = jwtTokenProvider.validateToken(token);

		assertThat(isValid).isFalse();
	}

	@DisplayName("변조된 토큰은 유효하지 않아야 한다.")
	@Test
	void validateToken_shouldReturnFalse_whenTokenIsTampered() {
		String token = jwtTokenProvider.generateAccessToken("ddobang", false, "도방유저");

		String tampered = token.substring(0, token.length() - 1) + "x";

		boolean isValid = jwtTokenProvider.validateToken(tampered);

		assertThat(isValid).isFalse();
	}

	@Test
	void validateToken_shouldReturnTrue_whenValidToken() {
		String token = jwtTokenProvider.generateAccessToken("ddobang", false, "도방유저");

		boolean isValid = jwtTokenProvider.validateToken(token);

		assertThat(isValid).isTrue();
	}
}