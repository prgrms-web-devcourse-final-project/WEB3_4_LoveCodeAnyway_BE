package com.ddobang.backend.domain.upload.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ddobang.backend.domain.upload.service.S3UploadService;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

/**
 * S3UploadController
 * <p></p>
 * @author 100minha
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
public class S3UploadController {

	private final S3UploadService s3UploadService;

	@PostMapping(value = "/image/{diaryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SuccessResponse<Void>> uploadImage(
		@PathVariable(required = false) Long diaryId,
		@RequestParam(defaultValue = "NONE") FileUploadTarget target,
		@RequestParam(required = false) MultipartFile file
	) throws IOException {
		s3UploadService.uploadImage(diaryId, target, file);
		return ResponseFactory.created("이미지 업로드에 성공했습니다.");
	}

	@PostMapping(value = "/attachment/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SuccessResponse<Void>> uploadAttachment(
		@PathVariable Long postId,
		@RequestParam(required = false) MultipartFile[] files
	) throws IOException {
		s3UploadService.uploadAttachment(postId, files);
		return ResponseFactory.created("첨부파일 업로드에 성공했습니다.");
	}
}
