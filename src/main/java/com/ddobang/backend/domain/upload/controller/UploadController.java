package com.ddobang.backend.domain.upload.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.upload.service.UploadService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/uploads")
@Tag(name = "UploadController", description = "파일 업로드 관리 API")
public class UploadController {
	private final UploadService uploadService;
}
