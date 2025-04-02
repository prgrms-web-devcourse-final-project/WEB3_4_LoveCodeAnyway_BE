package com.ddobang.backend.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
			.authorizeHttpRequests(auth -> auth
				.anyRequest().permitAll()
			)
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuth2SuccessHandler)
			)
			.headers(headers -> headers.frameOptions(
					frameOptions -> frameOptions.sameOrigin()
				)
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);

		return http.build();
	}
}
