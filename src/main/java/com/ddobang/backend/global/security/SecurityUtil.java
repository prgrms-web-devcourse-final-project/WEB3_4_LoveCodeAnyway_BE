package com.ddobang.backend.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ddobang.backend.global.exception.jwt.JwtErrorCode;
import com.ddobang.backend.global.exception.jwt.JwtException;

@Component
public class SecurityUtil {

	/**
	 * 현재 로그인한 사용자 정보 반환
	 */
	public static CustomUserDetails getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
			throw new JwtException(JwtErrorCode.INVALID_PRINCIPAL);
		}

		return (CustomUserDetails)auth.getPrincipal();
	}

	/**
	 * 현재 로그인한 사용자 ID 반환
	 */
	public static Long getCurrentMemberId() {
		return getCurrentUser().memberId();
	}

	/**
	 * 현재 로그인한 사용자 닉네임 반환
	 */
	public static String getCurrentNickname() {
		return getCurrentUser().nickname();
	}

	/**
	 * 현재 사용자가 관리자 여부
	 */
	public static boolean isCurrentUserAdmin() {
		return getCurrentUser().isAdmin();
	}
}

