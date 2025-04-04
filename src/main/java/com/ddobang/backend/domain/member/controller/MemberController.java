package com.ddobang.backend.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.auth.exception.AuthException;
import com.ddobang.backend.domain.auth.exception.OAuth2ErrorCode;
import com.ddobang.backend.domain.member.dto.request.SignupRequest;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.util.CookieUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/signup")
	public ResponseEntity<?> completeSignup(
		@Valid @RequestBody SignupRequest request,
		@CookieValue("signupToken") String signupToken,
		HttpServletResponse response
	) {
		try {
			Claims claims = jwtTokenProvider.getClaims(signupToken);
			String kakaoId = claims.get("kakaoId", String.class);
			String nickname = request.nickname();

			memberService.completeSignup(kakaoId, nickname);

			String accessToken = jwtTokenProvider.generateAccessToken(nickname, false);
			String refreshToken = jwtTokenProvider.generateRefreshToken(nickname, false);

			response.addCookie(CookieUtil.createAccessTokenCookie(accessToken));
			response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken));

			return ResponseFactory.ok("회원가입이 완료되었습니다.");
		} catch (Exception e) {
			log.error("회원가입 처리 중 오류 발생", e);
			throw new AuthException(OAuth2ErrorCode.OAUTH2_MEMBER_PROCESS_FAIL);
		}
	}
}
