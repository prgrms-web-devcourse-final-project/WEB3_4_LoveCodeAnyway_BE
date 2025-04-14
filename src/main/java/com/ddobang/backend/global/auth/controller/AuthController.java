package com.ddobang.backend.global.auth.controller;

import com.ddobang.backend.global.auth.dto.request.SignupRequest;
import com.ddobang.backend.global.auth.service.AuthService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<SuccessResponse<Void>> signup(
		@RequestBody @Valid SignupRequest request,
		@CookieValue(name = "signupToken") String signupToken,
		HttpServletResponse response
	) {
		authService.signup(response, request, signupToken);
		return ResponseFactory.created("회원가입을 성공하였습니다.");
	}

	// 로그인
	@GetMapping("/login")
	public void kakaoLogin(
			String redirectUrl,
			HttpServletResponse response
	) throws IOException {
		response.sendRedirect("/oauth2/authorization/kakao?redirectUrl=" + redirectUrl);
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<SuccessResponse<Void>> logout(HttpServletResponse response) {
		authService.logout(response);
		return ResponseFactory.ok("로그아웃을 성공하였습니다.");
	}
}
