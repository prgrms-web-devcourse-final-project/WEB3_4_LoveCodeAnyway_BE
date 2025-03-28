package com.ddobang.backend.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageRequestDto {

	@NotNull(message = "수신자 ID는 필수입니다.")
	private Long receiverId;

	@NotBlank(message = "제목은 필수입니다.")
	private String title;

	@NotBlank(message = "내용은 필수입니다.")
	private String content;
}
