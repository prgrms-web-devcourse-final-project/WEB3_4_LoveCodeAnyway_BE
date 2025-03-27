package com.ddobang.backend.domain.store.dto;

import com.ddobang.backend.domain.store.entity.Store;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * StoreSaveRequest
 * @author 100minha
 */
@Builder
public record StoreRequest(
	Long regionId,
	@NotBlank(message = "매장 이름은 공백일 수 없습니다.")
	String name,
	String address,
	String phoneNumber,
	String siteUrl,
	Store.Status status
) {
}
