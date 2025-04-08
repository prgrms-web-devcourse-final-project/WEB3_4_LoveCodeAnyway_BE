package com.ddobang.backend.domain.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.service.RegionService;
import com.ddobang.backend.domain.store.dto.StoreRequest;
import com.ddobang.backend.domain.store.dto.StoreResponse;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.exception.StoreErrorCode;
import com.ddobang.backend.domain.store.exception.StoreException;
import com.ddobang.backend.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;

	private final RegionService regionService;

	@Transactional
	public void saveForAdmin(StoreRequest storeRequest) {
		Region region = regionService.findById(storeRequest.regionId());
		storeRepository.save(Store.of(storeRequest, region));
	}

	@Transactional(readOnly = true)
	public Store findById(Long id) {
		return storeRepository.findById(id)
			.orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
	}

	@Transactional
	public void modify(Long id, StoreRequest storeRequest) {
		Store store = findById(id);

		Region region = regionService.findById(storeRequest.regionId());
		store.modify(storeRequest, region);
	}

	/**
	 * 매장 삭제 처리 메서드
	 * Store.Status 를 DELETED 로 변경
	 * @param id 삭제 대상 매장 id
	 */
	@Transactional
	public void delete(Long id) {
		findById(id).delete();
	}

	@Transactional
	public Store saveForMember(Store store) {
		return storeRepository.save(store);
	}

	@Transactional(readOnly = true)
	public List<StoreResponse> getStoresByKeyword(String keyword) {
		return storeRepository.findStoresByKeyword(keyword);
	}
}
