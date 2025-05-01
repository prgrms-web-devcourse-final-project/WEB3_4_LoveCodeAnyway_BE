package com.ddobang.backend.global.security.jwt;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.ddobang.backend.global.exception.ErrorCode;
import com.ddobang.backend.global.exception.jwt.JwtException;
import com.ddobang.backend.global.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);

		} catch (JwtException e) {
			log.warn("JWT 예외 발생: {}", e.getMessage());
			sendErrorResponse(response, e.getErrorCode());
		}
	}

	private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType("application/json;charset=UTF-8");

		ErrorResponse errorResponse = ErrorResponse.of(errorCode); // 에러 담기

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(errorResponse);

		response.getWriter().write(json);
	}
}
