package com.ddobang.backend.domain.theme.dto.request;

import java.util.List;

import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.global.validation.annotation.ValidEnum;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * ThemeForAdminRequest
 * 사용자 전용 테마 등록 요청 DTO
 * @author 100minha
 */
public record ThemeForAdminRequest(
	@NotNull(message = "매장 정보는 필수입니다.")
	Long storeId,
	List<Long> tagIds,
	@NotBlank(message = "테마 이름은 필수입니다.")
	String name,
	String description,
	@Min(value = 1, message = "난이도는 1 이상이어야 합니다.")
	@Max(value = 5, message = "난이도는 5 이하이어야 합니다.")
	Float officialDifficulty,
	@Positive(message = "소요 시간은 0 이상이어야 합니다.")
	Integer runtime,
	@Min(value = 1, message = "최소 인원은 1 이상이어야 합니다.")
	@Max(value = 8, message = "최소 인원은 8 이하이어야 합니다.")
	Integer minParticipants,
	@Min(value = 1, message = "최대 인원은 1 이상이어야 합니다.")
	@Max(value = 20, message = "최대 인원은 20 이하이어야 합니다.")
	Integer maxParticipants,
	@Positive(message = "가격은 0 이상이어야 합니다.")
	Integer price,
	@ValidEnum(enumClass = Theme.Status.class, message = "유효하지 않은 status 입력입니다.")
	String status,
	String reservationUrl,
	String thumbnailUrl
) {
}
