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

		// MvcRequestMatcher를 사용하여 URL 패턴 매칭
		MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector).servletPath("/api/v1");

		http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session
				-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 인가 정책
			.authorizeHttpRequests(auth -> {
				auth
					.requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()
					.requestMatchers("/error", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
						"/webjars/**").permitAll()
					.requestMatchers("/admin/login").permitAll()
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers(mvc.pattern("/members/check-nickname")).permitAll()
					.requestMatchers(mvc.pattern("/themes/**")).permitAll()
					.anyRequest().hasAnyRole("MEMBER", "ADMIN");
			})

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
