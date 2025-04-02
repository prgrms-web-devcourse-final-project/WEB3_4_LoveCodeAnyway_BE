package com.ddobang.backend.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ddobang.backend.global.security.jwt.JwtAuthenticationFilter;
import com.ddobang.backend.global.security.jwt.JwtExceptionFilter;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.security.oauth.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session
				-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// 인가 정책
			.authorizeHttpRequests(auth -> auth
				.anyRequest().permitAll() // 개발 중에만 우선순위 허용
				.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
				.requestMatchers("/api/v1/**").hasAnyRole("MEMBER", "ADMIN")
			)

			// OAuth2 로그인 설정
			.oauth2Login(oauth -> oauth
				.successHandler(oAuth2SuccessHandler)
			)

			// 인증 필터 등록
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class)

			// 개발용 설정
			.headers(headers -> headers.frameOptions(frameOptions
				-> frameOptions.sameOrigin())
			);

		return http.build();
	}
}
