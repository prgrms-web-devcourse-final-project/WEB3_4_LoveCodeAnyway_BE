package com.ddobang.backend.global.security.oauth;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.global.auth.service.AuthService;
import com.ddobang.backend.global.exception.oauth2.OAuth2ErrorCode;
import com.ddobang.backend.global.exception.oauth2.OAuth2Exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final MemberService memberService;
	private final AuthService authService;

	// OAuth2 로그인 성공 시 호출되는 메서드
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		// 인증된 사용자 정보 가져오기
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		String kakaoId = (String)oAuth2User.getAttribute("id");
		if (kakaoId == null) {
			throw new OAuth2Exception(OAuth2ErrorCode.OAUTH2_MISSING_ID);
		}

		// 로그에 카카오 ID와 닉네임 출력
		log.info("OAuth2 로그인 성공 - kakaoId: {}", kakaoId);

		// 카카오 ID로 회원 정보 조회
		Optional<Member> optionalMember = memberService.findByKakaoId(kakaoId);

		// 회원 정보가 없으면 신규 회원으로 처리
		if (optionalMember.isPresent()) {
			// 기존 회원
			authService.handleLoginSuccess(response, kakaoId);
			log.info("기존 회원 로그인 처리 완료");
			response.sendRedirect("/"); // 메인 페이지
		} else {
			// 신규 회원
			authService.handlePreSignup(response, kakaoId);
			log.info("신규 회원 - 회원가입용 토큰 쿠키 전송 완료");
			response.sendRedirect("/signup"); // 회원가입 페이지로 리다이렉트
		}
	}
}
