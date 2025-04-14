package com.ddobang.backend.global.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.global.security.jwt.JwtAuthenticationFilter;
import com.ddobang.backend.global.security.jwt.JwtExceptionFilter;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.security.oauth.CustomAuthorizationRequestResolver;
import com.ddobang.backend.global.security.oauth.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;

	/**
	 * SecurityFilterChain 설정
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(
		HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
			.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (REST API에서는 CSRF 필요 없음)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안함

			// JWT 인증 필터 등록
			.exceptionHandling(ex -> ex
				.defaultAuthenticationEntryPointFor(
					new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), // 401 에러 처리
					new AntPathRequestMatcher("/api/**") // API 요청에 대해 401 에러 처리
				)
			)

			// 인증 및 인가 필터 등록
			.authorizeHttpRequests(auth -> {
				auth
					// OAuth2 로그인 관련
					.requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**")
					.permitAll()

					// 로그인, 회원가입 관련 URL & API 접근 설정
					.requestMatchers("/login", "/signup")
					.permitAll() // 로그인 및 회원가입 페이지
					.requestMatchers(HttpMethod.GET, "/api/v1/auth/login")
					.permitAll() // 카카오 로그인
					.requestMatchers(HttpMethod.POST, "/api/v1/auth/signup")
					.permitAll() // 회원가입
					.requestMatchers(HttpMethod.GET, "/api/v1/members/check-nickname")
					.permitAll() // 닉네임 중복 체크
					.requestMatchers(HttpMethod.GET, "/api/v1/members/tags")
					.permitAll() // 사용자 태그 목록 조회

					// 관리자 관련 URL & API 접근 설정
					.requestMatchers("/admin/login")
					.permitAll() // 관리자 로그인 페이지 접근 허용
					.requestMatchers(HttpMethod.POST, "/api/v1/auth/admin-login")
					.permitAll() // 관리자 로그인 API 접근 허용
					.requestMatchers("/admin/**", "/api/v1/admin/**")
					.hasRole("ADMIN") // 그 외 관리자만 접근 가능

					// 인증이 필요없는 API 접근 설정
					.requestMatchers(HttpMethod.GET, "/api/v1/regions")
					.permitAll() // 지역 소분류 조회
					.requestMatchers(HttpMethod.POST, "/api/v1/themes")
					.permitAll() // 테마 - 목록 필터 조회
					.requestMatchers(HttpMethod.GET, "/api/v1/themes/*")
					.permitAll() // 테마 - 조회 API
					.requestMatchers(HttpMethod.GET, "/api/v1/themes/*/parties")
					.permitAll() // 테마별 - 모임 목록 조회
					.requestMatchers(HttpMethod.GET, "/api/v1/parties/main")
					.permitAll() // 모임 - 메인 페이지 조회
					.requestMatchers(HttpMethod.POST, "/api/v1/parties/search")
					.permitAll() // 모임 - 목록 조회
					.requestMatchers(HttpMethod.GET, "/api/v1/parties/*")
					.permitAll() // 모임 - 상세 조회

					// Swagger, 오류 페이지 관련 URL & API 접근 설정
					.requestMatchers("/error", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
						"/webjars/**")
					.permitAll()

					.requestMatchers(HttpMethod.OPTIONS, "/**")
					.permitAll() // CORS preflight 요청 허용
					.requestMatchers("/actuator/**")
					.permitAll() // Actuator API 접근 허용

					// 그 외 인증 필요
					.anyRequest()
					.hasAnyRole("USER", "ADMIN");
			})

			// OAuth2 로그인 설정
			.oauth2Login(
				oauth2Login -> oauth2Login
					.successHandler(oAuth2SuccessHandler)
					.authorizationEndpoint(
						authorizationEndpoint ->
							authorizationEndpoint
								.authorizationRequestResolver(customAuthorizationRequestResolver)
					)
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
			"https://localhost:3000",                       // 로컬 개발용
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
