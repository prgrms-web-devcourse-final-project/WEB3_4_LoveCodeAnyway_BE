package com.ddobang.backend.global.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.global.exception.jwt.JwtErrorCode;
import com.ddobang.backend.global.exception.jwt.JwtException;
import com.ddobang.backend.global.security.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;

	// JWT 토큰을 사용하지 않는 API 목록
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.equals("/api/v1/auth/signup") ||
			path.equals("/api/v1/members/check-nickname") ||
			path.startsWith("/swagger") ||
			path.startsWith("/v3/api-docs") ||
			path.equals("/error");
	}

	// JWT 토큰을 검증하고 인증 정보를 SecurityContext에 저장
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		try {
			String token = jwtTokenProvider.resolveAccessToken(request);

			if (token != null && jwtTokenProvider.isValidToken(token, JwtTokenType.ACCESS)) {
				String nickname = jwtTokenProvider.extractNickname(token);
				Member member = memberService.getByNickname(nickname);

				// 비밀번호가 null이 아니고 빈 문자열이 아닐 경우 관리자
				boolean isAdmin = member.getPassword() != null && !member.getPassword().isBlank();

				// SecurityContext에 인증 정보 저장
				CustomUserDetails userDetails = new CustomUserDetails(
					member.getId(), member.getNickname(), isAdmin);

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (JwtException e) {
			log.error("[JwtAuthenticationFilter] JWT 처리 에러", e);
			throw new JwtException(JwtErrorCode.TOKEN_INVALID);
		}

		filterChain.doFilter(request, response);
	}
}
