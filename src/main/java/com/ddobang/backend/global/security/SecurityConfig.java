package com.ddobang.backend.global.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.global.security.jwt.JwtAuthenticationFilter;
import com.ddobang.backend.global.security.jwt.JwtExceptionFilter;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.security.oauth.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	/**
	 * SecurityFilterChain 설정
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(
		HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session
				-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 인가 정책
			.authorizeHttpRequests(auth -> {
				auth
					// OAuth2 로그인 관련
					.requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll() // OAuth2 로그인 관련
					.requestMatchers("/signup", "/api/v1/auth/signup").permitAll() // 회원가입 페이지
					.requestMatchers("/login", "/api/v1/auth/login").permitAll() // 카카오 로그인 URL

					// Swagger, 오류 페이지
					.requestMatchers("/error", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
						"/webjars/**").permitAll()

					// 관리자 관련 API
					.requestMatchers("/admin/login").permitAll() // 로그인만 공개
					.requestMatchers("/admin/**").hasRole("ADMIN")
					// TODO : 관리자 관련 API 추가

					// 닉네임 중복 체크
					.requestMatchers("/api/v1/members/check-nickname").permitAll()

					// 로그인 관련 API 허용
					.requestMatchers(HttpMethod.GET, "/api/v1/auth/login").permitAll() // 카카오 로그인 URL

					// 공개 API
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight 요청 허용
					.requestMatchers(HttpMethod.GET, "/api/v1/regions").permitAll()      // 지역 조회
					.requestMatchers(HttpMethod.POST, "/api/v1/auth/signup").permitAll() // 회원가입
					.requestMatchers("/api/v1/themes").permitAll()
					.requestMatchers("/api/v1/themes/*").permitAll()
					.requestMatchers("/api/v1/parties").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/v1/auth/signup").permitAll() // 회원가입
					.requestMatchers("/api/v1/parties/*").permitAll()
					.requestMatchers("/api/v1/stores/*").permitAll()
					// Acuator API
					.requestMatchers("/actuator/**").permitAll()
					// 인증 필요 API
					.anyRequest().hasAnyRole("USER", "ADMIN");

			})

			// OAuth2 로그인 설정
			.oauth2Login(oauth -> oauth
				.successHandler(oAuth2SuccessHandler)
			)

			// 인증 필터 등록
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, memberService),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class)

			// 개발용 설정
			.headers(headers -> headers.frameOptions(frameOptions
				-> frameOptions.sameOrigin())
			);

		return http.build();
	}

	/**
	 * CORS 설정
	 * @return CorsConfigurationSource
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
			"http://localhost:3000",                       // 로컬 개발용
			"https://www.ddobang.site",                        // 배포 주소
			"https://ddobang.site",                        // 배포 주소
			"https://web-1-2-pitching-mate-fe.vercel.app"  // Vercel 배포 주소
		));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true); // 인증 정보 전송 허용

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
