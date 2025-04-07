package com.ddobang.backend.global.auth.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {

	@GetMapping("/login")
	public void kakaoLogin(HttpServletResponse response) throws IOException {
		response.sendRedirect("/oauth2/authorization/kakao");
	}
}
