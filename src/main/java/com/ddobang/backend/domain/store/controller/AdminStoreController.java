package com.ddobang.backend.domain.store.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.store.dto.StoreRequest;
import com.ddobang.backend.domain.store.dto.StoreResponse;
import com.ddobang.backend.domain.store.service.StoreService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * AdminStoreController
 * 관리자 전용 StoreController
 * @author 100minha
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/stores")
public class AdminStoreController {

	private final StoreService storeService;

	@Operation(summary = "관리자 전용 매장 등록 API")
	@PostMapping
	public ResponseEntity<SuccessResponse<Void>> saveStore(
		@RequestBody @Valid StoreRequest storeRequest) {

		storeService.saveForAdmin(storeRequest);
		return ResponseFactory.created("매장 등록에 성공했습니다.");
	}

	@Operation(summary = "관리자 전용 매장 수정 API")
	@PutMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> modifyStore(
		@PathVariable Long id,
		@RequestBody @Valid StoreRequest storeRequest) {

		storeService.modify(id, storeRequest);
		return ResponseFactory.ok(id + "번 매장 수정에 성공했습니다.");
	}

	@Operation(summary = "관리자 전용 매장 삭제 API")
	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> deleteStore(
		@PathVariable Long id) {

		storeService.delete(id);
		return ResponseFactory.ok(id + "번 매장 삭제에 성공했습니다.");
	}

	@Operation(summary = "관리자 전용 매장 검색 API", description = "테마를 저장할 때 매핑할 매장 검색")
	@GetMapping
	public ResponseEntity<SuccessResponse<List<StoreResponse>>> getStoresByKeyword(
		@RequestParam(defaultValue = "") String keyword
	) {
		return ResponseFactory.ok(storeService.getStoresByKeyword(keyword));
	}
}
