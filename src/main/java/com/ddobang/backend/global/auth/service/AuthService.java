package com.ddobang.backend.global.auth.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberTagMappingRepository;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.member.service.MemberTagService;
import com.ddobang.backend.global.auth.dto.request.SignupRequest;
import com.ddobang.backend.global.exception.oauth2.OAuth2ErrorCode;
import com.ddobang.backend.global.exception.oauth2.OAuth2Exception;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.security.jwt.JwtTokenType;
import com.ddobang.backend.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;
	private final MemberTagService memberTagService;
	private final MemberTagMappingRepository memberTagMappingRepository;
	private final CookieUtil cookieUtil;

	/**
	 * 로그인 성공 시 액세스 / 리프레시 토큰 생성 및 쿠키에 저장
	 */
	public void handleLoginSuccess(HttpServletResponse response, Member member) {
		boolean isAdmin = false;

		String accessToken = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, isAdmin);
		String refreshToken = jwtTokenProvider.generateToken(member, JwtTokenType.REFRESH, isAdmin);

		response.addCookie(cookieUtil.createAccessTokenCookie(accessToken));
		response.addCookie(cookieUtil.createRefreshTokenCookie(refreshToken));
	}

	/**
	 * 회원가입 시도 시 회원가입용 토큰 생성 및 쿠키에 저장
	 */
	public void handlePreSignup(HttpServletResponse response, String kakaoId) {
		if (kakaoId == null) {
			throw new OAuth2Exception(OAuth2ErrorCode.OAUTH2_MISSING_ID);
		}

		String signupToken = jwtTokenProvider.generateSignupToken(kakaoId);
		response.addCookie(cookieUtil.createSignupTokenCookie(signupToken));
	}

	/**
	 * 회원가입 완료 후 액세스 / 리프레시 토큰 발급 및 쿠키 저장
	 */
	@Transactional
	public void signup(HttpServletResponse response, SignupRequest request, String signupToken) {
		// 토큰 유효성 검사
		jwtTokenProvider.isValidToken(signupToken, JwtTokenType.SIGNUP);

		// 카카오 ID 추출
		String kakaoId = jwtTokenProvider.extractKakaoId(signupToken);

		// 회원 등록 (중복검사 + 저장 + 태그 매핑)
		Member member = memberService.registerMember(kakaoId, request);

		// 엑세스 / 리프레시 토큰 발급
		String accessToken = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);
		String refreshToken = jwtTokenProvider.generateToken(member, JwtTokenType.REFRESH, false);

		// 쿠키에 저장
		response.addCookie(cookieUtil.createAccessTokenCookie(accessToken));
		response.addCookie(cookieUtil.createRefreshTokenCookie(refreshToken));
	}

	/**
	 * 로그아웃 시 쿠키 삭제
	 */
	public void logout(HttpServletResponse response) {
		// accessToken 쿠키 삭제
		ResponseCookie accessToken = ResponseCookie.from("accessToken", "")
			.path("/")
			.httpOnly(true)
			.maxAge(0)
			.build();

		// refreshToken 쿠키 삭제
		ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "")
			.path("/")
			.httpOnly(true)
			.maxAge(0)
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());
	}
}
