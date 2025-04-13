package com.ddobang.backend.global.security.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.support.MemberTestFactory;

import jakarta.servlet.http.HttpServletRequest;

class JwtTokenProviderTest {

	private JwtTokenProvider jwtTokenProvider;
	private JwtTokenFactory tokenFactory;
	private JwtTokenValidator tokenValidator;
	private JwtPayloadExtractor payloadExtractor;
	private JwtCookieResolver cookieResolver;

	@BeforeEach
	void setUp() {
		tokenFactory = mock(JwtTokenFactory.class);
		tokenValidator = mock(JwtTokenValidator.class);
		payloadExtractor = mock(JwtPayloadExtractor.class);
		cookieResolver = mock(JwtCookieResolver.class);

		jwtTokenProvider = new JwtTokenProvider(
			tokenFactory,
			tokenValidator,
			payloadExtractor,
			cookieResolver
		);
	}

	@Test
	@DisplayName("토큰 유효성 검사 - 유효한 경우 true")
	void isValidToken_success() {
		String token = "valid.jwt.token";
		JwtTokenType type = JwtTokenType.ACCESS;

		given(tokenValidator.isValidToken(token, type)).willReturn(true);

		boolean result = jwtTokenProvider.isValidToken(token, type);

		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("카카오 ID 추출")
	void extractKakaoId_success() {
		String token = "signup.token.jwt";
		given(payloadExtractor.extractKakaoId(token)).willReturn("kakao123");

		String kakaoId = jwtTokenProvider.extractKakaoId(token);

		assertThat(kakaoId).isEqualTo("kakao123");
	}

	@Test
	@DisplayName("닉네임 추출")
	void extractNickname_success() {
		String token = "access.token.jwt";
		given(payloadExtractor.extractNickname(token)).willReturn("재영");

		String nickname = jwtTokenProvider.extractNickname(token);

		assertThat(nickname).isEqualTo("재영");
	}

	@Test
	@DisplayName("isAdmin 값 추출")
	void extractIsAdmin_success() {
		String token = "jwt.token";
		given(payloadExtractor.extractIsAdmin(token)).willReturn(true);

		boolean result = jwtTokenProvider.extractIsAdmin(token);

		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("AccessToken 쿠키 추출")
	void resolveAccessToken_success() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		given(cookieResolver.resolveAccessToken(request)).willReturn("access.token.jwt");

		String token = jwtTokenProvider.resolveAccessToken(request);

		assertThat(token).isEqualTo("access.token.jwt");
	}

	@Test
	@DisplayName("토큰 생성 위임 확인")
	void generateToken_shouldDelegate() {
		Member member = MemberTestFactory.Basic();
		given(tokenFactory.generateToken(member, JwtTokenType.ACCESS, false))
			.willReturn("access.jwt.token");

		String token = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		assertThat(token).isEqualTo("access.jwt.token");
	}
}
