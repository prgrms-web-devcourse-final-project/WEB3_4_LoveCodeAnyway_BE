package com.ddobang.backend.global.auth;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ddobang.backend.domain.diary.repository.DiaryRepository;
import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.global.auth.dto.request.SignupRequest;
import com.ddobang.backend.global.security.jwt.JwtTokenFactory;
import com.ddobang.backend.global.security.jwt.JwtTokenType;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("회원가입 통합 테스트 (성공 케이스)")
class AuthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private JwtTokenFactory jwtTokenFactory;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private DiaryRepository diaryRepository;

	// 테스트 전 데이터베이스 초기화
	@BeforeEach
	void setUp() {
		diaryRepository.deleteAll();
		memberRepository.deleteAll();
	}

	// 테스트를 위한 데이터 생성
	private SignupRequest createSignupRequest() {
		return new SignupRequest(
			"또방이", Gender.BLIND, "자기소개입니다",
			List.of(1L, 2L), "https://img.url"
		);
	}

	// 테스트를 위한 JWT 토큰 쿠키 생성
	private Cookie createSignupTokenCookie() {
		String token = jwtTokenFactory.generateToken("12345678", JwtTokenType.SIGNUP, false);
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

		mockMvc.perform(post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.cookie(createSignupTokenCookie()))
			.andExpect(cookie().exists("accessToken"))
			.andExpect(cookie().exists("refreshToken"))
			.andExpect(jsonPath("$.message").value("회원가입을 성공하였습니다."))
			.andExpect(status().isCreated()); // 201 Created 응답

		List<Member> members = memberRepository.findAll();
		assertThat(members).hasSize(1);
		assertThat(members.getFirst().getNickname()).isEqualTo("또방이");
	}

	@Test
	@DisplayName("카카오 로그인 진입 시 OAuth2 인증 URL로 리다이렉트")
	void login_redirectToKakaoAuthorization() throws Exception {
		mockMvc.perform(get("/api/v1/auth/login"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string("Location", "/oauth2/authorization/kakao"));
	}
}
