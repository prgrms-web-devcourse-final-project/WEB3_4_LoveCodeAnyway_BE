package com.ddobang.backend.global.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * CORS 설정이 정상 동작하는지 확인하는 통합 테스트
 * - 프론트엔드에서 보내는 OPTIONS 요청에 대해 허용 헤더가 포함되어 응답되는지 확인한다.
 * @author Jay Lim
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsConfigIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("CORS - 프론트엔드에서 보내는 OPTIONS 요청에 대해 허용 헤더가 포함되어 응답된다")
	void CORS01() throws Exception {
		// given
		String frontendOrigin = "https://www.ddobang.site/";

		// when & then
		mockMvc.perform(options("/api/v1/regions") // CORS 요청을 보내는 URL(예: /api/v1/regions)
				.header(HttpHeaders.ORIGIN, frontendOrigin)
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, frontendOrigin))
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,PATCH,OPTIONS"))
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
	}

}
