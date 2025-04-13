package com.ddobang.backend.global.security.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "custom.oauth2.redirect.frontend")
public class OAuth2RedirectProperties {

	@NotBlank
	private String main; // 메인 페이지 URL

	@NotBlank
	private String signup; // 회원가입 페이지 URL
}
