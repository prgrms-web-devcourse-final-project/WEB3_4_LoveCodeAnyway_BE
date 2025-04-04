package com.ddobang.backend.global.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

	// 테스트 환경에서는 JWT 인증을 사용하지 않음
	@Bean
	public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.anyRequest().permitAll() // 모든 요청 허용
			)
			.sessionManagement(AbstractHttpConfigurer::disable); // 세션도 꺼도 됨 (JWT 안 쓸 거니까)

		return http.build();
	}
}
