package com.ddobang.backend.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MessageRequestDto {

	@NotNull(message = "수신자 ID는 필수입니다.")
	private Long receiverId;

	@NotBlank(message = "내용은 필수입니다.")
	private String content;
}
