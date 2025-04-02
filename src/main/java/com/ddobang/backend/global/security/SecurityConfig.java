package com.ddobang.backend.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

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
	public SecurityFilterChain securityFilterChain(
		HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

		MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector).servletPath("/api/v1");

		http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session
				-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// 인가 정책
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(mvc.pattern("/api/v1/error")).permitAll()
				.requestMatchers(mvc.pattern("/error")).permitAll()
				.requestMatchers(mvc.pattern("/v3/api-docs/**")).permitAll()
				.requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
				.requestMatchers(mvc.pattern("/swagger-resources/**")).permitAll()
				.requestMatchers(mvc.pattern("/webjars/**")).permitAll()
				.requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN")
				.requestMatchers(mvc.pattern("/**")).hasAnyRole("MEMBER", "ADMIN")
				.anyRequest().permitAll()
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
