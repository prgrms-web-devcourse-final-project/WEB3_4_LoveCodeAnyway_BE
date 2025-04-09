package com.ddobang.backend.global.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberTag;
import com.ddobang.backend.domain.member.entity.MemberTagMapping;
import com.ddobang.backend.domain.member.exception.MemberErrorCode;
import com.ddobang.backend.domain.member.exception.MemberException;
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

	/**
	 * 로그인 성공 시 액세스 / 리프레시 토큰 생성 및 쿠키에 저장
	 */
	public void handleLoginSuccess(HttpServletResponse response, String kakaoId) {
		boolean isAdmin = false;

		String accessToken = jwtTokenProvider.generateToken(kakaoId, JwtTokenType.ACCESS, isAdmin);
		String refreshToken = jwtTokenProvider.generateToken(kakaoId, JwtTokenType.REFRESH, isAdmin);

		response.addCookie(CookieUtil.createAccessTokenCookie(accessToken));
		response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken));
	}

	/**
	 * 회원가입 시도 시 회원가입용 토큰 생성 및 쿠키에 저장
	 */
	public void handlePreSignup(HttpServletResponse response, String kakaoId) {
		if (kakaoId == null) {
			throw new OAuth2Exception(OAuth2ErrorCode.OAUTH2_MISSING_ID);
		}

		String signupToken = jwtTokenProvider.generateToken(kakaoId, JwtTokenType.SIGNUP, false);
		response.addCookie(CookieUtil.createSignupTokenCookie(signupToken));
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

		// 이미 가입된 회원인지 확인
		if (memberService.existsByKakaoId(kakaoId)) {
			throw new AuthException(AuthErrorCode.ALREADY_REGISTERED);
		}

		// 닉네임 중복 검사 (이중 체크)
		if (memberService.existsByNickname(request.nickname())) {
			throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
		}

		// 회원 엔티티 생성 및 등록
		Member member = request.toEntity(kakaoId);
		memberService.save(member);

		// 태그 매핑 처리
		List<MemberTag> tags = memberTagService.findAllByIds(request.tags());
		for (MemberTag tag : tags) {
			MemberTagMapping mapping = new MemberTagMapping(member, tag);
			memberTagMappingRepository.save(mapping);
		}

		// 엑세스 / 리프레시 토큰 발급
		String accessToken = jwtTokenProvider.generateToken(member.getNickname(), JwtTokenType.ACCESS, false);
		String refreshToken = jwtTokenProvider.generateToken(member.getNickname(), JwtTokenType.REFRESH, false);

		// 쿠키에 저장
		response.addCookie(CookieUtil.createAccessTokenCookie(accessToken));
		response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken));
	}
}
