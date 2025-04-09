package com.ddobang.backend.global.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ddobang.backend.global.exception.GlobalErrorCode;
import com.ddobang.backend.global.exception.ServiceException;
import com.ddobang.backend.global.util.CustomUserDetails;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	// 필터 적용을 제회할 URL 패턴을 설정
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.equals("/api/v1/auth/signup") ||
			path.equals("/api/v1/members/check-nickname") ||
			path.startsWith("/swagger") ||
			path.startsWith("/v3/api-docs") ||
			path.equals("/error");
	}

	// JWT 토큰을 검증하고 인증 객체를 SecurityContext에 등록
	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			String token = jwtTokenProvider.resolveAccessToken(request);

			if (token != null && jwtTokenProvider.isValidToken(token, JwtTokenType.ACCESS)) {
				String nickname = jwtTokenProvider.extractNickname(token);
				boolean isAdmin = jwtTokenProvider.extractIsAdmin(token);

				CustomUserDetails userDetails = new CustomUserDetails(nickname,
					isAdmin); // 닉네임과 권한을 사용하여 UserDetails 객체 생성
				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(authentication);
				log.debug("인증 객체 등록 완료 - 닉네임: {}", nickname);
			}
		} catch (JwtException e) {
			log.warn("JWT 처리 중 예외 발생: {}", e.getMessage());
			throw new ServiceException(GlobalErrorCode.UNAUTHORIZED);
		}

		filterChain.doFilter(request, response);
	}
}
