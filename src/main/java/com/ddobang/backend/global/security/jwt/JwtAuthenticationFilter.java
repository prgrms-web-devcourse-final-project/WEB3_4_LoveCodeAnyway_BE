package com.ddobang.backend.global.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ddobang.backend.global.exception.GlobalErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

import io.jsonwebtoken.JwtException;
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

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			String token = jwtTokenProvider.resolveAccessToken(request);

			if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
				String nickName = jwtTokenProvider.getNickname(token);
				boolean isAdmin = jwtTokenProvider.getIsAdmin(token);

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(nickName, null, jwtTokenProvider.getAuthorities(isAdmin));

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				log.debug("인증 객체 등록 완료 - 닉네임: {}", nickName);
			}

		} catch (JwtException e) {
			log.warn("JWT 처리 중 예외 발생: {}", e.getMessage());
			throw new ServiceException(GlobalErrorCode.UNAUTHORIZED);
		}

		filterChain.doFilter(request, response);
	}
}
