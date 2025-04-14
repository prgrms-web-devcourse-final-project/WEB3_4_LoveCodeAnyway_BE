package com.ddobang.backend.global.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberTagMappingRepository;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.member.service.MemberTagService;
import com.ddobang.backend.global.auth.dto.request.SignupRequest;
import com.ddobang.backend.global.exception.auth.AuthErrorCode;
import com.ddobang.backend.global.exception.auth.AuthException;
import com.ddobang.backend.global.exception.oauth2.OAuth2ErrorCode;
import com.ddobang.backend.global.exception.oauth2.OAuth2Exception;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.security.jwt.JwtTokenType;
import com.ddobang.backend.global.util.CookieUtil;
import com.ddobang.backend.support.MemberTestFactory;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@Mock
	private MemberService memberService;
	@Mock
	private MemberTagService memberTagService;
	@Mock
	private MemberTagMappingRepository memberTagMappingRepository;
	@Mock
	private HttpServletResponse response;
	@Mock
	private CookieUtil cookieUtil;
	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("기존 회원 로그인 성공 - 액세스/리프레시 토큰 발급")
	void loginSuccess() {
		// given
		String accessToken = "access-token";
		String refreshToken = "refresh-token";
		Member member = MemberTestFactory.Basic();
		given(jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false)).willReturn(accessToken);
		given(jwtTokenProvider.generateToken(member, JwtTokenType.REFRESH, false)).willReturn(refreshToken);

		// when / then
		assertThatCode(() -> authService.handleLoginSuccess(response, member))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("신규 회원 - 회원가입용 토큰 발급")
	void preSignupSuccess() {
		// given
		String kakaoId = "12345678";
		// when / then
		assertThatCode(() -> authService.handlePreSignup(response, kakaoId))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("카카오 ID가 null이면 예외 발생")
	void throwExceptionWhenKakaoIdIsNull() {
		// given
		String kakaoId = null;

		// when / then
		assertThatThrownBy(() -> authService.handlePreSignup(response, kakaoId))
			.isInstanceOf(OAuth2Exception.class)
			.hasMessage(OAuth2ErrorCode.OAUTH2_MISSING_ID.getMessage());
	}

	@Test
	@DisplayName("회원가입 성공 - 토큰 발급 및 쿠키 저장")
	void signupSuccess() {
		// given
		String signupToken = "signup-token";
		String kakaoId = "kakao789";
		String nickname = "testnick";
		List<Long> tagIds = List.of(1L, 2L);
		SignupRequest request = new SignupRequest(nickname, Gender.BLIND, "소개글", tagIds, "image-url");
		Member member = request.toEntity(kakaoId);

		given(jwtTokenProvider.isValidToken(signupToken, JwtTokenType.SIGNUP)).willReturn(true);
		given(jwtTokenProvider.extractKakaoId(signupToken)).willReturn(kakaoId);
		given(memberService.registerMember(kakaoId, request)).willReturn(member);
		given(jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false)).willReturn("access-token");
		given(jwtTokenProvider.generateToken(member, JwtTokenType.REFRESH, false)).willReturn("refresh-token");

		// when / then
		assertThatCode(() -> authService.signup(response, request, signupToken))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("회원가입 시 이미 존재하는 회원이면 예외")
	void signupFailIfAlreadyExists() {
		// given
		String signupToken = "signup-token";
		String kakaoId = "kakao789";
		String nickname = "testnick";
		SignupRequest request = new SignupRequest(nickname, Gender.BLIND, "소개글입니다.", List.of(), "image-url");

		given(jwtTokenProvider.isValidToken(signupToken, JwtTokenType.SIGNUP)).willReturn(true);
		given(jwtTokenProvider.extractKakaoId(signupToken)).willReturn(kakaoId);
		given(memberService.registerMember(kakaoId, request))
			.willThrow(new AuthException(AuthErrorCode.ALREADY_REGISTERED));

		// when / then
		assertThatThrownBy(() -> authService.signup(response, request, signupToken))
			.isInstanceOf(AuthException.class)
			.hasMessage(AuthErrorCode.ALREADY_REGISTERED.getMessage());
	}
}
