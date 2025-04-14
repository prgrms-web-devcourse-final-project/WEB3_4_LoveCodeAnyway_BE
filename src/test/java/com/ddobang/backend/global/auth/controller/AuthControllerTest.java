package com.ddobang.backend.global.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import com.ddobang.backend.global.auth.service.AuthService;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@TestConfiguration
	static class TestConfig {
		@Bean
		public AuthService authService() {
			return Mockito.mock(AuthService.class);
		}
	}

	@Test
	@DisplayName("t1 - 카카오 로그인 리다이렉트 확인")
	void t1() throws Exception {
		mockMvc.perform(get("/api/v1/auth/login"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("**/oauth2/authorization/kakao"));
	}
}
