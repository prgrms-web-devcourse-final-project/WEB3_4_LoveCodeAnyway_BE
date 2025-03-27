package com.ddobang.backend.domain.diary.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.dto.response.DiaryDto;
import com.ddobang.backend.domain.diary.service.DiaryService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diaries")
@Tag(name = "DiaryController", description = "탈출일지 관리 API")
public class DiaryController {
	private final DiaryService diaryService;

	@Operation(summary = "탈출일지 등록", description = "새로운 탈출일지를 등록합니다.")
	@PostMapping
	public ResponseEntity<SuccessResponse<DiaryDto>> write(
		@RequestBody @Valid DiaryRequestDto diaryRequestDto
	) {
		DiaryDto diaryDto = diaryService.write(diaryRequestDto);

		return ResponseFactory.created(
			"탈출일지 등록에 성공했습니다.",
			diaryDto
		);
	}

	@Operation(summary = "탈출일지 단건조회", description = "탈출일지 id를 기준으로 특정 탈출일지를 가져옵니다.")
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<DiaryDto>> getItem(
		@PathVariable long id
	) {
		DiaryDto diaryDto = diaryService.getItem(id);

		return ResponseFactory.ok(diaryDto);
	}
}
