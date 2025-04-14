package com.ddobang.backend.global.security.oauth;

import java.io.IOException;

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
		Object kakaoIdObj = oAuth2User.getAttribute("id");

		// 카카오 ID가 null인 경우 예외 처리
		if (kakaoIdObj == null) {
			throw new OAuth2Exception(OAuth2ErrorCode.OAUTH2_MISSING_ID);
		}

		// 카카오 ID를 문자열로 변환
		String kakaoId = kakaoIdObj.toString();

		// 로그에 카카오 ID와 닉네임 출력
		log.info("OAuth2 로그인 성공 - kakaoId: {}", kakaoId);

		// 카카오 ID로 회원 정보 조회
		Member searchMember = memberService.getByKakaoId(kakaoId);
		if (searchMember == null) {
			log.info("신규 회원 - 카카오 ID로 회원 정보 조회 결과 없음");
		} else {
			log.info("기존 회원 - 카카오 ID로 회원 정보 조회 결과: {}", searchMember);
		}

		String baseRedirectUrl = request.getParameter("state");

		if (baseRedirectUrl == null) {
			log.info("state 파라미터가 null입니다.");
			throw new OAuth2Exception(OAuth2ErrorCode.OAUTH2_MISSING_STATE);
		}

		if (searchMember != null) {
			// 기존 회원: 토큰 생성 및 쿠키 저장
			authService.handleLoginSuccess(response, searchMember);
			log.info("기존 회원 로그인 처리 완료");
			response.sendRedirect(baseRedirectUrl); // 메인 페이지로 리다이렉트
		} else {
			// 신규 회원: 회원가입용 토큰 쿠키 전송
			authService.handlePreSignup(response, kakaoId);
			log.info("신규 회원 - 회원가입용 토큰 쿠키 전송 완료");
			response.sendRedirect(baseRedirectUrl + "/signup"); // 회원가입 페이지로 리다이렉트
		}
	}
}
