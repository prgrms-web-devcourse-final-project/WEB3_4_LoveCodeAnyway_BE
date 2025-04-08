package com.ddobang.backend.domain.store.dto;

import com.ddobang.backend.domain.store.entity.Store;

/**
 * StoreResponse
 * 관리자 전용 매장 조회 응답 DTO
 * @author 100minha
 */
public record StoreResponse(
	Long id,
	String name,
	String address,
	Store.Status status
) {
}
