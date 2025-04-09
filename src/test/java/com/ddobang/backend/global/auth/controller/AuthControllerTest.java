package com.ddobang.backend.global.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 로그인 관련 API 테스트
 * @author Jay Lim
 */
@WebMvcTest(AuthController.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("t1 - 카카오 로그인 리다이렉트 확인")
	void t1() throws Exception {
		mockMvc.perform(get("/api/v1/auth/login"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("**/oauth2/authorization/kakao"));
	}
}
