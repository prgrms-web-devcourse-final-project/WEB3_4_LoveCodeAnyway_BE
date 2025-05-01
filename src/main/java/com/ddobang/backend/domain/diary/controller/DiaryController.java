package com.ddobang.backend.domain.diary.controller;

import com.ddobang.backend.domain.diary.dto.request.DiaryFilterRequest;
import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.dto.response.DiaryDto;
import com.ddobang.backend.domain.diary.dto.response.DiaryListDto;
import com.ddobang.backend.domain.diary.service.DiaryService;
import com.ddobang.backend.domain.theme.dto.request.ThemeForMemberRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.global.response.PageDto;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diaries")
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

	@Operation(summary = "탈출일지 다건조회", description = "필터를 기반으로 사용자의 전체 탈출일지 목록을 가져옵니다.")
	@PostMapping("/list")
	public ResponseEntity<SuccessResponse<PageDto<DiaryListDto>>> getAllItems(
		@RequestBody @Valid DiaryFilterRequest request,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		return ResponseFactory.ok(
			PageDto.of(
				diaryService.getAllItems(request, page, pageSize)
			)
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

	@Operation(summary = "탈출일지 수정", description = "탈출일지 id를 기준으로 특정 탈출일지를 가져와 수정합니다.")
	@PutMapping("/{id}")
	public ResponseEntity<SuccessResponse<DiaryDto>> modify(
		@PathVariable long id,
		@RequestBody @Valid DiaryRequestDto diaryRequestDto
	) {
		DiaryDto diaryDto = diaryService.modify(id, diaryRequestDto);

		return ResponseFactory.ok(
			"%d번 탈출일지 수정에 성공했습니다.".formatted(id),
			diaryDto
		);
	}

	@Operation(summary = "탈출일지 삭제", description = "탈출일지 id를 기준으로 특정 탈출일지를 가져와 삭제합니다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> delete(
		@PathVariable long id
	) {
		diaryService.delete(id);

		return ResponseFactory.ok("%d번 탈출일지 삭제에 성공했습니다.".formatted(id));
	}

	@Operation(summary = "탈출일지 월별 다건조회", description = "필터를 기반으로 사용자의 전체 탈출일지 목록을 가져옵니다.")
	@GetMapping
	public ResponseEntity<SuccessResponse<List<DiaryListDto>>> getDiariesByMonth(
		@RequestParam(defaultValue = "0") int year,
		@RequestParam(defaultValue = "0") int month
	) {
		return ResponseFactory.ok(
			diaryService.getDiariesByMonth(year, month)
		);
	}

	@Operation(summary = "탈출일지에서 테마 등록", description = "등록되지 않은 테마를 탈출일지에 작성하기 위해 테마를 등록합니다.")
	@PostMapping("/theme")
	public ResponseEntity<SuccessResponse<SimpleThemeResponse>> saveThemeForDiary(
		@RequestBody @Valid ThemeForMemberRequest request
	) {
		SimpleThemeResponse theme = diaryService.saveThemeForDiary(request);

		return ResponseFactory.created(
			"테마 등록에 성공했습니다.",
			theme
		);
	}

}
