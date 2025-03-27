package com.ddobang.backend.domain.store.service;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.service.RegionService;
import com.ddobang.backend.domain.store.dto.StoreRequest;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;

	private final RegionService regionService;

	public void saveForAdmin(StoreRequest storeRequest) {
		Region region = regionService.findById(storeRequest.regionId());
		storeRepository.save(Store.of(storeRequest, region));
	}
}
