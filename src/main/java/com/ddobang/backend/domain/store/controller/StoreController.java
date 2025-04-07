package com.ddobang.backend.domain.store.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
// TODO: 사용하지 않을 시 삭제
public class StoreController {
	private final StoreService storeService;
}
