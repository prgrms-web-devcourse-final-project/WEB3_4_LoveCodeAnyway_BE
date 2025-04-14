package com.ddobang.backend.global.auth;

import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.global.auth.dto.request.SignupRequest;
import com.ddobang.backend.global.security.jwt.JwtTokenFactory;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.security.jwt.JwtTokenType;
import com.ddobang.backend.support.MemberTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("AuthController 통합 테스트")
class AuthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private JwtTokenFactory jwtTokenFactory;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ObjectMapper objectMapper;

	// 테스트를 위한 데이터 생성
	private SignupRequest createSignupRequest() {
		return new SignupRequest(
			"또방이", Gender.BLIND, "자기소개입니다",
			List.of(1L, 2L), "https://img.url"
		);
	}

	// 테스트를 위한 JWT 토큰 쿠키 생성
	private Cookie createSignupTokenCookie() {
		Member member = MemberTestFactory.Basic();
		String token = jwtTokenFactory.generateToken(member, JwtTokenType.SIGNUP, false);
		Cookie cookie = new Cookie("signupToken", token);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

	@Test
	@DisplayName("회원가입 요청 시 토큰 쿠키와 함께 200 응답 + 사용자 저장")
	void signupSuccess() throws Exception {
		SignupRequest request = createSignupRequest();
		String json = objectMapper.writeValueAsString(request); // JSON으로 변환
		int memberSize = (int)memberRepository.count();

		mockMvc.perform(post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.cookie(createSignupTokenCookie()))
			.andExpect(cookie().exists("accessToken"))
			.andExpect(cookie().exists("refreshToken"))
			.andExpect(jsonPath("$.message").value("회원가입을 성공하였습니다."))
			.andExpect(status().isCreated()); // 201 Created 응답

		List<Member> members = memberRepository.findAll();
		assertThat(members).hasSize(memberSize + 1);
		assertThat(members.getLast().getNickname()).isEqualTo("또방이");
	}

	@Test
	@DisplayName("카카오 로그인 진입 시 OAuth2 인증 URL로 리다이렉트")
	void login_redirectToKakaoAuthorization() throws Exception {
		mockMvc.perform(get("/api/v1/auth/login?redirectUrl=/"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string("Location", "/oauth2/authorization/kakao?redirectUrl=/"));
	}

	@Test
	@DisplayName("[성공] 로그아웃 시 쿠키 삭제 후 인증 API 접근 시 401이 발생한다")
	void logout_thenUnauthorizedOnProtectedApi() throws Exception {
		// given: 멤버 및 토큰 생성
		Member member = memberRepository.save(MemberTestFactory.Basic());
		String accessToken = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		// when: 로그아웃 요청 (accessToken 쿠키 삭제)
		mockMvc.perform(post("/api/v1/auth/logout")
				.cookie(new Cookie("accessToken", accessToken))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("로그아웃을 성공하였습니다."));

		// then: 로그아웃 후 인증이 필요한 API에 접근 시 401
		mockMvc.perform(get("/api/v1/members/me"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("[성공] 로그아웃 시 access/refresh 토큰이 모두 삭제된다")
	void logout_shouldRemoveBothTokens() throws Exception {
		Member member = memberRepository.save(MemberTestFactory.Basic());
		String accessToken = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);
		String refreshToken = jwtTokenProvider.generateToken(member, JwtTokenType.REFRESH, false);

		mockMvc.perform(post("/api/v1/auth/logout")
				.cookie(new Cookie("accessToken", accessToken))
				.cookie(new Cookie("refreshToken", refreshToken))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("로그아웃을 성공하였습니다."))
			.andExpect(header().stringValues("Set-Cookie", Matchers.hasItems(
				Matchers.containsString("accessToken=;"),
				Matchers.containsString("refreshToken=;")
			)));
	}
}
