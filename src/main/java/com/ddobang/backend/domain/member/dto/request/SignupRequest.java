package com.ddobang.backend.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
	@Pattern(
		regexp = "^(?!_)(?!.*?_$)[가-힣a-zA-Z0-9_]+$",
		message = "닉네임은 한글, 영문, 숫자, 밑줄(_)만 사용할 수 있으며, 밑줄(_)로 시작하거나 끝날 수 없습니다.")
	String nickname
) {
}
