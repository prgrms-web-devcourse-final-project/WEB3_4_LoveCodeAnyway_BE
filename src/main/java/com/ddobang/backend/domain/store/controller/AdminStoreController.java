package com.ddobang.backend.domain.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.store.dto.StoreRequest;
import com.ddobang.backend.domain.store.service.StoreService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * AdminStoreController
 * 관리자 전용 StoreController
 * @author 100minha
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/stores")
public class AdminStoreController {

	private final StoreService storeService;

	@PostMapping
	public ResponseEntity<SuccessResponse<Void>> saveStore(
		@RequestBody @Valid StoreRequest storeRequest) {

		storeService.saveForAdmin(storeRequest);
		return ResponseFactory.created("매장 등록에 성공했습니다.");
	}
}
