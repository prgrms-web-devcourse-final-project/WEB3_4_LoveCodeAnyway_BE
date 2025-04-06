package com.ddobang.backend.domain.upload.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ddobang.backend.domain.upload.service.UploadService;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/uploads")
@Tag(name = "UploadController", description = "파일 업로드 관리 API")
public class UploadController {
	private final UploadService uploadService;

	@Operation(summary = "파일 업로드", description = "새로운 파일을 업로드합니다.")
	@PostMapping("/{parentId}")
	public ResponseEntity<SuccessResponse<Void>> upload(
		@PathVariable long parentId,
		@RequestParam(defaultValue = "NONE") FileUploadTarget target,
		@RequestParam MultipartFile[] files
	) throws IOException {
		uploadService.upload(parentId, target, files);

		return ResponseFactory.created(
			"파일 저장에 성공했습니다."
		);
	}
}