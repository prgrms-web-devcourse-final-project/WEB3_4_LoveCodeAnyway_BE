package com.ddobang.backend.domain.diary.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.dto.request.DiaryFilterRequest;
import com.ddobang.backend.domain.diary.dto.response.DiaryDto;
import com.ddobang.backend.domain.diary.dto.response.DiaryListDto;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.exception.DiaryException;
import com.ddobang.backend.domain.diary.service.DiaryService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class DiaryControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private DiaryService diaryService;

	@Test
	@DisplayName("탈출일지 등록")
	void t1() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries")
				.content("""
					{
						"themeId": 1,
						"timeType": "elapsed"
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryDto lastDiary = diaryService.getItemsAll(1, 1).getContent().get(0);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("write"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("탈출일지 등록에 성공했습니다."))
			.andExpect(jsonPath("$.data.id").value(lastDiary.id()))
			.andExpect(jsonPath("$.data.themeId").value(lastDiary.themeId()))
			.andExpect(jsonPath("$.data.themeName").value(lastDiary.themeName()))
			.andExpect(jsonPath("$.data.storeName").value(lastDiary.storeName()))
			.andExpect(jsonPath("$.data.thumbnailUrl").value(lastDiary.thumbnailUrl()))
			.andExpect(jsonPath("$.data.imageUrl").value(lastDiary.imageUrl()))
			.andExpect(jsonPath("$.data.escapeDate").value(lastDiary.escapeDate()))
			.andExpect(jsonPath("$.data.participants").value(lastDiary.participants()))
			.andExpect(jsonPath("$.data.difficulty").value(lastDiary.difficulty()))
			.andExpect(jsonPath("$.data.fear").value(lastDiary.fear()))
			.andExpect(jsonPath("$.data.activity").value(lastDiary.activity()))
			.andExpect(jsonPath("$.data.satisfaction").value(lastDiary.satisfaction()))
			.andExpect(jsonPath("$.data.production").value(lastDiary.production()))
			.andExpect(jsonPath("$.data.story").value(lastDiary.story()))
			.andExpect(jsonPath("$.data.question").value(lastDiary.question()))
			.andExpect(jsonPath("$.data.interior").value(lastDiary.interior()))
			.andExpect(jsonPath("$.data.deviceRatio").value(lastDiary.deviceRatio()))
			.andExpect(jsonPath("$.data.hintCount").value(lastDiary.hintCount()))
			.andExpect(jsonPath("$.data.escapeResult").value(lastDiary.escapeResult()))
			.andExpect(jsonPath("$.data.elapsedTime").value(lastDiary.elapsedTime()))
			.andExpect(jsonPath("$.data.review").value(lastDiary.review()))
			.andExpect(jsonPath("$.data.createdAt").exists())
			.andExpect(jsonPath("$.data.modifiedAt").exists());
	}

	@Test
	@DisplayName("탈출일지 등록, theme id, timeType이 없을 때")
	void t1_1() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries")
				.content("""
					{}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("write"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("themeId")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("테마를 선택해주세요.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("timeType")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("탈출 시간 타입을 선택해주세요.")));
	}

	@Test
	@DisplayName("탈출일지 등록, 테마 평가 항목이 정해진 범위의 값이 아닐 때")
	void t1_2() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries")
				.content("""
					{
						"themeId": 1,
						"timeType": "elapsed",
						"difficulty": 6,
						"fear": 6,
						"activity": 6,
						"satisfaction": 6,
						"production": 6,
						"story": 6,
						"question": 6,
						"interior": 6,
						"deviceRatio": 200
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("write"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("difficulty")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("난이도는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("fear")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("공포도는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("activity")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("활동성은 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("satisfaction")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("만족도는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("production")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("연출은 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("story")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("스토리는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("question")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("문제 구성은 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("interior")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("인테리어는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("deviceRatio")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("장치 비율은 최대 100% 이하여야 합니다.")));
	}

	@Test
	@DisplayName("탈출일지 등록, timeType이 정해진 값이 아닐 때")
	void t1_3() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries")
				.content("""
					{
						"themeId": 1,
						"timeType": "WRONG TYPE",
						"elapsedTime": "65:00"
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("write"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("진행 시간인지, 남은 시간인지 확인해주세요."));
	}

	@Test
	@DisplayName("탈출일지 등록, 탈출 시간이 00:00의 형식이 아닐 때")
	void t1_4() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries")
				.content("""
					{
						"themeId": 1,
						"timeType": "remaining",
						"elapsedTime": "WRONG TIME"
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("write"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못 된 시간 형식입니다."));
	}

	@Test
	@DisplayName("탈출일지 단건 조회")
	void t2() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/diaries/1"))
			.andDo(print());

		Diary diary = diaryService.findById(1);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getItem"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(diary.getId()))
			.andExpect(jsonPath("$.data.themeId").value(diary.getTheme().getId()))
			.andExpect(jsonPath("$.data.themeName").value(diary.getTheme().getName()))
			.andExpect(jsonPath("$.data.storeName").value(diary.getTheme().getStore().getName()))
			.andExpect(jsonPath("$.data.thumbnailUrl").value(diary.getTheme().getThumbnailUrl()))
			.andExpect(jsonPath("$.data.imageUrl").value(diary.getImageUrl()))
			.andExpect(jsonPath("$.data.escapeDate").value(diary.getEscapeDate().toString()))
			.andExpect(jsonPath("$.data.participants").value(diary.getParticipants()))
			.andExpect(jsonPath("$.data.difficulty").value(diary.getDiaryStat().getDifficulty()))
			.andExpect(jsonPath("$.data.fear").value(diary.getDiaryStat().getFear()))
			.andExpect(jsonPath("$.data.activity").value(diary.getDiaryStat().getActivity()))
			.andExpect(jsonPath("$.data.satisfaction").value(diary.getDiaryStat().getSatisfaction()))
			.andExpect(jsonPath("$.data.production").value(diary.getDiaryStat().getProduction()))
			.andExpect(jsonPath("$.data.story").value(diary.getDiaryStat().getStory()))
			.andExpect(jsonPath("$.data.question").value(diary.getDiaryStat().getQuestion()))
			.andExpect(jsonPath("$.data.interior").value(diary.getDiaryStat().getInterior()))
			.andExpect(jsonPath("$.data.deviceRatio").value(diary.getDiaryStat().getDeviceRatio()))
			.andExpect(jsonPath("$.data.hintCount").value(diary.getDiaryStat().getHintCount()))
			.andExpect(jsonPath("$.data.escapeResult").value(diary.getDiaryStat().isEscapeResult()))
			.andExpect(jsonPath("$.data.elapsedTime").value(diary.getDiaryStat().getElapsedTime()))
			.andExpect(jsonPath("$.data.review").value(diary.getReview()))
			.andExpect(jsonPath("$.data.createdAt").exists())
			.andExpect(jsonPath("$.data.modifiedAt").exists());
	}

	@Test
	@DisplayName("탈출일지 단건 조회, 존재하지 않는 번호의 탈출일지 조회")
	void t2_1() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/diaries/99999999"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getItem"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("탈출일지를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("탈출일지 수정")
	void t3() throws Exception {
		ResultActions resultActions = mvc
			.perform(put("/diaries/1")
				.content("""
					{
						"themeId": 1,
						"imageUrl": "https://placehold.co/320x320?text=o_o",
						"escapeDate": "2025-02-20",
						"participants": "내 칭구1, 내 칭구2",
						"difficulty": 5,
						"fear": 1,
						"activity": 3,
						"satisfaction": 4,
						"production": 3,
						"story": 3,
						"question": 4,
						"interior": 4,
						"deviceRatio": 50,
						"hintCount": 0,
						"escapeResult": true,
						"timeType": "elapsed",
						"elapsedTime": "65:00",
						"review": "완전 완전 재밌었다!!"
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		Diary diary = diaryService.findById(1L);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("1번 탈출일지 수정에 성공했습니다."))
			.andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.themeId").value(1))
			.andExpect(jsonPath("$.data.themeName").value(diary.getTheme().getName()))
			.andExpect(jsonPath("$.data.thumbnailUrl").value(diary.getTheme().getThumbnailUrl()))
			.andExpect(jsonPath("$.data.storeName").value(diary.getTheme().getStore().getName()))
			.andExpect(jsonPath("$.data.imageUrl").value("https://placehold.co/320x320?text=o_o"))
			.andExpect(jsonPath("$.data.escapeDate").value("2025-02-20"))
			.andExpect(jsonPath("$.data.participants").value("내 칭구1, 내 칭구2"))
			.andExpect(jsonPath("$.data.difficulty").value(5))
			.andExpect(jsonPath("$.data.fear").value(1))
			.andExpect(jsonPath("$.data.activity").value(3))
			.andExpect(jsonPath("$.data.satisfaction").value(4))
			.andExpect(jsonPath("$.data.production").value(3))
			.andExpect(jsonPath("$.data.story").value(3))
			.andExpect(jsonPath("$.data.question").value(4))
			.andExpect(jsonPath("$.data.interior").value(4))
			.andExpect(jsonPath("$.data.deviceRatio").value(50))
			.andExpect(jsonPath("$.data.hintCount").value(0))
			.andExpect(jsonPath("$.data.escapeResult").value(true))
			.andExpect(jsonPath("$.data.elapsedTime").value(3900))
			.andExpect(jsonPath("$.data.review").value("완전 완전 재밌었다!!"))
			.andExpect(jsonPath("$.data.createdAt").exists())
			.andExpect(jsonPath("$.data.modifiedAt").exists());
	}

	@Test
	@DisplayName("탈출일지 수정, 존재하지 않는 번호의 탈출일지 수정")
	void t3_1() throws Exception {
		ResultActions resultActions = mvc
			.perform(put("/diaries/99999999")
				.content("""
					{
						"themeId": 1,
						"imageUrl": "https://placehold.co/320x320?text=o_o",
						"escapeDate": "2025-02-20",
						"participants": "내 칭구1, 내 칭구2",
						"difficulty": 5,
						"fear": 1,
						"activity": 3,
						"satisfaction": 4,
						"production": 3,
						"story": 3,
						"question": 4,
						"interior": 4,
						"deviceRatio": 50,
						"hintCount": 0,
						"escapeResult": true,
						"timeType": "elapsed",
						"elapsedTime": 34500,
						"review": "완전 완전 재밌었다!!"
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("탈출일지를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("탈출일지 수정, theme id가 없을 때")
	void t3_2() throws Exception {
		ResultActions resultActions = mvc
			.perform(put("/diaries/1")
				.content("""
					{
						"imageUrl": "https://placehold.co/320x320?text=o_o",
						"escapeDate": "2025-02-20",
						"hintCount": 0,
						"escapeResult": true,
						"timeType": "elapsed",
						"elapsedTime": 34500,
						"review": "완전 완전 재밌었다!!"
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.errors[0].field").value("themeId"))
			.andExpect(jsonPath("$.errors[0].message").value("테마를 선택해주세요."));
	}

	@Test
	@DisplayName("탈출일지 수정, 테마 평가 항목이 정해진 범위의 값이 아닐 때")
	void t3_3() throws Exception {
		ResultActions resultActions = mvc
			.perform(put("/diaries/1")
				.content("""
					{
						"themeId": 1,
						"difficulty": 6,
						"fear": 6,
						"activity": 6,
						"satisfaction": 6,
						"production": 6,
						"story": 6,
						"question": 6,
						"interior": 6,
						"deviceRatio": 200
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("difficulty")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("난이도는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("fear")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("공포도는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("activity")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("활동성은 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("satisfaction")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("만족도는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("production")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("연출은 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("story")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("스토리는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("question")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("문제 구성은 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("interior")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("인테리어는 최대 5 이하여야 합니다.")))
			.andExpect(jsonPath("$.errors[*].field").value(Matchers.hasItem("deviceRatio")))
			.andExpect(jsonPath("$.errors[*].message").value(Matchers.hasItem("장치 비율은 최대 100% 이하여야 합니다.")));
	}

	@Test
	@DisplayName("탈출일지 수정, timeType이 정해진 값이 아닐 때")
	void t3_4() throws Exception {
		ResultActions resultActions = mvc
			.perform(put("/diaries/1")
				.content("""
					{
						"themeId": 1,
						"timeType": "WRONG TYPE",
						"elapsedTime": "65:00"
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("진행 시간인지, 남은 시간인지 확인해주세요."));
	}

	@Test
	@DisplayName("탈출일지 수정, 탈출 시간이 00:00의 형식이 아닐 때")
	void t3_5() throws Exception {
		ResultActions resultActions = mvc
			.perform(put("/diaries/1")
				.content("""
					{
						"themeId": 1,
						"timeType": "remaining",
						"elapsedTime": "WRONG TIME"
					}
					""".stripIndent())
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못 된 시간 형식입니다."));
	}

	@Test
	@DisplayName("탈출일지 삭제")
	void t4() throws Exception {
		ResultActions resultActions = mvc
			.perform(delete("/diaries/1"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("1번 탈출일지 삭제에 성공했습니다."));

		assertThatThrownBy(() -> diaryService.findById(1))
			.isInstanceOf(DiaryException.class)
			.hasMessage("탈출일지를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("탈출일지 삭제, 존재하지 않는 번호의 탈출일지 삭제")
	void t4_1() throws Exception {
		ResultActions resultActions = mvc
			.perform(delete("/diaries/99999999"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("탈출일지를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("탈출일지 다건 조회")
	void t5() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries/list")
				.content("{}")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryFilterRequest request = DiaryFilterRequest.builder()
			.regionId(null)
			.tagNames(null)
			.startDate(null)
			.endDate(null)
			.isSuccess(null)
			.isNoHint(null)
			.keyword(null)
			.build();

		Page<DiaryListDto> diariesPage = diaryService
			.getAllItems(request, 0, 10);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getAllItems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.currentPageNumber").value(1))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.totalPages").value(diariesPage.getTotalPages()))
			.andExpect(jsonPath("$.data.totalItems").value(diariesPage.getTotalElements()));

		List<DiaryListDto> diaries = diariesPage.getContent();

		for (int i = 0; i < diaries.size(); i++) {
			DiaryListDto diary = diaries.get(i);

			resultActions
				.andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(diary.id()))
				.andExpect(jsonPath("$.data.items[%d].themeId".formatted(i)).value(diary.themeId()))
				.andExpect(jsonPath("$.data.items[%d].themeName".formatted(i)).value(diary.themeName()))
				.andExpect(jsonPath("$.data.items[%d].thumbnailUrl".formatted(i)).value(diary.thumbnailUrl()))
				.andExpect(jsonPath("$.data.items[%d].tags".formatted(i))
					.value(Matchers.containsInAnyOrder(diary.tags().toArray())))
				.andExpect(jsonPath("$.data.items[%d].storeName".formatted(i)).value(diary.storeName()))
				.andExpect(jsonPath("$.data.items[%d].escapeDate".formatted(i)).value(diary.escapeDate().toString()))
				.andExpect(jsonPath("$.data.items[%d].elapsedTime".formatted(i)).value(diary.elapsedTime()))
				.andExpect(jsonPath("$.data.items[%d].hintCount".formatted(i)).value(diary.hintCount()))
				.andExpect(jsonPath("$.data.items[%d].escapeResult".formatted(i)).value(diary.escapeResult()));
		}
	}

	@Test
	@DisplayName("탈출일지 다건 조회, with 테마명 검색")
	void t5_1() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries/list")
				.content("""
					{
						"keyword": "테마 1"
					}
					""")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryFilterRequest request = DiaryFilterRequest.builder()
			.regionId(null)
			.tagNames(null)
			.startDate(null)
			.endDate(null)
			.isSuccess(null)
			.isNoHint(null)
			.keyword("테마 1")
			.build();

		Page<DiaryListDto> diariesPage = diaryService
			.getAllItems(request, 0, 10);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getAllItems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.currentPageNumber").value(1))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.totalPages").value(diariesPage.getTotalPages()))
			.andExpect(jsonPath("$.data.totalItems").value(diariesPage.getTotalElements()));

		List<DiaryListDto> diaries = diariesPage.getContent();

		for (int i = 0; i < diaries.size(); i++) {
			DiaryListDto diary = diaries.get(i);

			resultActions
				.andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(diary.id()))
				.andExpect(jsonPath("$.data.items[%d].themeId".formatted(i)).value(diary.themeId()))
				.andExpect(jsonPath("$.data.items[%d].themeName".formatted(i)).value(diary.themeName()))
				.andExpect(jsonPath("$.data.items[%d].thumbnailUrl".formatted(i)).value(diary.thumbnailUrl()))
				.andExpect(jsonPath("$.data.items[%d].tags".formatted(i))
					.value(Matchers.containsInAnyOrder(diary.tags().toArray())))
				.andExpect(jsonPath("$.data.items[%d].storeName".formatted(i)).value(diary.storeName()))
				.andExpect(jsonPath("$.data.items[%d].escapeDate".formatted(i)).value(diary.escapeDate().toString()))
				.andExpect(jsonPath("$.data.items[%d].elapsedTime".formatted(i)).value(diary.elapsedTime()))
				.andExpect(jsonPath("$.data.items[%d].hintCount".formatted(i)).value(diary.hintCount()))
				.andExpect(jsonPath("$.data.items[%d].escapeResult".formatted(i)).value(diary.escapeResult()));
		}
	}

	@Test
	@DisplayName("탈출일지 다건 조회, with 지역 검색")
	void t5_2() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries/list")
				.content("""
					{
						"regionId": [1]
					}
					""")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryFilterRequest request = DiaryFilterRequest.builder()
			.regionId(List.of(1L))
			.tagNames(null)
			.startDate(null)
			.endDate(null)
			.isSuccess(null)
			.isNoHint(null)
			.keyword(null)
			.build();

		Page<DiaryListDto> diariesPage = diaryService
			.getAllItems(request, 0, 10);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getAllItems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.currentPageNumber").value(1))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.totalPages").value(diariesPage.getTotalPages()))
			.andExpect(jsonPath("$.data.totalItems").value(diariesPage.getTotalElements()));

		List<DiaryListDto> diaries = diariesPage.getContent();

		for (int i = 0; i < diaries.size(); i++) {
			DiaryListDto diary = diaries.get(i);

			resultActions
				.andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(diary.id()))
				.andExpect(jsonPath("$.data.items[%d].themeId".formatted(i)).value(diary.themeId()))
				.andExpect(jsonPath("$.data.items[%d].themeName".formatted(i)).value(diary.themeName()))
				.andExpect(jsonPath("$.data.items[%d].thumbnailUrl".formatted(i)).value(diary.thumbnailUrl()))
				.andExpect(jsonPath("$.data.items[%d].tags".formatted(i))
					.value(Matchers.containsInAnyOrder(diary.tags().toArray())))
				.andExpect(jsonPath("$.data.items[%d].storeName".formatted(i)).value(diary.storeName()))
				.andExpect(jsonPath("$.data.items[%d].escapeDate".formatted(i)).value(diary.escapeDate().toString()))
				.andExpect(jsonPath("$.data.items[%d].elapsedTime".formatted(i)).value(diary.elapsedTime()))
				.andExpect(jsonPath("$.data.items[%d].hintCount".formatted(i)).value(diary.hintCount()))
				.andExpect(jsonPath("$.data.items[%d].escapeResult".formatted(i)).value(diary.escapeResult()));
		}
	}

	@Test
	@DisplayName("탈출일지 다건 조회, with 장르 검색")
	void t5_3() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries/list")
				.content("""
					{
						"tagNames": ["공포", "판타지"]
					}
					""")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryFilterRequest request = DiaryFilterRequest.builder()
			.regionId(null)
			.tagNames(List.of("공포", "판타지"))
			.startDate(null)
			.endDate(null)
			.isSuccess(null)
			.isNoHint(null)
			.keyword(null)
			.build();

		Page<DiaryListDto> diariesPage = diaryService
			.getAllItems(request, 0, 10);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getAllItems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.currentPageNumber").value(1))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.totalPages").value(diariesPage.getTotalPages()))
			.andExpect(jsonPath("$.data.totalItems").value(diariesPage.getTotalElements()));

		List<DiaryListDto> diaries = diariesPage.getContent();

		for (int i = 0; i < diaries.size(); i++) {
			DiaryListDto diary = diaries.get(i);

			resultActions
				.andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(diary.id()))
				.andExpect(jsonPath("$.data.items[%d].themeId".formatted(i)).value(diary.themeId()))
				.andExpect(jsonPath("$.data.items[%d].themeName".formatted(i)).value(diary.themeName()))
				.andExpect(jsonPath("$.data.items[%d].thumbnailUrl".formatted(i)).value(diary.thumbnailUrl()))
				.andExpect(jsonPath("$.data.items[%d].tags".formatted(i))
					.value(Matchers.containsInAnyOrder(diary.tags().toArray())))
				.andExpect(jsonPath("$.data.items[%d].storeName".formatted(i)).value(diary.storeName()))
				.andExpect(jsonPath("$.data.items[%d].escapeDate".formatted(i)).value(diary.escapeDate().toString()))
				.andExpect(jsonPath("$.data.items[%d].elapsedTime".formatted(i)).value(diary.elapsedTime()))
				.andExpect(jsonPath("$.data.items[%d].hintCount".formatted(i)).value(diary.hintCount()))
				.andExpect(jsonPath("$.data.items[%d].escapeResult".formatted(i)).value(diary.escapeResult()));
		}
	}

	@Test
	@DisplayName("탈출일지 다건 조회, with 기간 검색")
	void t5_4() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries/list")
				.content("""
					{
						"startDate": "2024-02-20",
						"endDate": "2024-05-20"
					}
					""")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryFilterRequest request = DiaryFilterRequest.builder()
			.regionId(null)
			.tagNames(null)
			.startDate(LocalDate.of(2024, 2, 20))
			.endDate(LocalDate.of(2024, 5, 20))
			.isSuccess(null)
			.isNoHint(null)
			.keyword(null)
			.build();

		Page<DiaryListDto> diariesPage = diaryService
			.getAllItems(request, 0, 10);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getAllItems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.currentPageNumber").value(1))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.totalPages").value(diariesPage.getTotalPages()))
			.andExpect(jsonPath("$.data.totalItems").value(diariesPage.getTotalElements()));

		List<DiaryListDto> diaries = diariesPage.getContent();

		for (int i = 0; i < diaries.size(); i++) {
			DiaryListDto diary = diaries.get(i);

			resultActions
				.andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(diary.id()))
				.andExpect(jsonPath("$.data.items[%d].themeId".formatted(i)).value(diary.themeId()))
				.andExpect(jsonPath("$.data.items[%d].themeName".formatted(i)).value(diary.themeName()))
				.andExpect(jsonPath("$.data.items[%d].thumbnailUrl".formatted(i)).value(diary.thumbnailUrl()))
				.andExpect(jsonPath("$.data.items[%d].tags".formatted(i))
					.value(Matchers.containsInAnyOrder(diary.tags().toArray())))
				.andExpect(jsonPath("$.data.items[%d].storeName".formatted(i)).value(diary.storeName()))
				.andExpect(jsonPath("$.data.items[%d].escapeDate".formatted(i)).value(diary.escapeDate().toString()))
				.andExpect(jsonPath("$.data.items[%d].elapsedTime".formatted(i)).value(diary.elapsedTime()))
				.andExpect(jsonPath("$.data.items[%d].hintCount".formatted(i)).value(diary.hintCount()))
				.andExpect(jsonPath("$.data.items[%d].escapeResult".formatted(i)).value(diary.escapeResult()));
		}
	}

	@Test
	@DisplayName("탈출일지 다건 조회, with 성공한 테마만 검색")
	void t5_5() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries/list")
				.content("""
					{
						"isSuccess": "success"
					}
					""")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryFilterRequest request = DiaryFilterRequest.builder()
			.regionId(null)
			.tagNames(null)
			.startDate(null)
			.endDate(null)
			.isSuccess("success")
			.isNoHint(null)
			.keyword(null)
			.build();

		Page<DiaryListDto> diariesPage = diaryService
			.getAllItems(request, 0, 10);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getAllItems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.currentPageNumber").value(1))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.totalPages").value(diariesPage.getTotalPages()))
			.andExpect(jsonPath("$.data.totalItems").value(diariesPage.getTotalElements()));

		List<DiaryListDto> diaries = diariesPage.getContent();

		for (int i = 0; i < diaries.size(); i++) {
			DiaryListDto diary = diaries.get(i);

			resultActions
				.andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(diary.id()))
				.andExpect(jsonPath("$.data.items[%d].themeId".formatted(i)).value(diary.themeId()))
				.andExpect(jsonPath("$.data.items[%d].themeName".formatted(i)).value(diary.themeName()))
				.andExpect(jsonPath("$.data.items[%d].thumbnailUrl".formatted(i)).value(diary.thumbnailUrl()))
				.andExpect(jsonPath("$.data.items[%d].tags".formatted(i))
					.value(Matchers.containsInAnyOrder(diary.tags().toArray())))
				.andExpect(jsonPath("$.data.items[%d].storeName".formatted(i)).value(diary.storeName()))
				.andExpect(jsonPath("$.data.items[%d].escapeDate".formatted(i)).value(diary.escapeDate().toString()))
				.andExpect(jsonPath("$.data.items[%d].elapsedTime".formatted(i)).value(diary.elapsedTime()))
				.andExpect(jsonPath("$.data.items[%d].hintCount".formatted(i)).value(diary.hintCount()))
				.andExpect(jsonPath("$.data.items[%d].escapeResult".formatted(i)).value(diary.escapeResult()));
		}
	}

	@Test
	@DisplayName("탈출일지 다건 조회, with 노힌트 테마만 검색")
	void t5_6() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries/list")
				.content("""
					{
						"isNoHint": true
					}
					""")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryFilterRequest request = DiaryFilterRequest.builder()
			.regionId(null)
			.tagNames(null)
			.startDate(null)
			.endDate(null)
			.isSuccess(null)
			.isNoHint(true)
			.keyword(null)
			.build();

		Page<DiaryListDto> diariesPage = diaryService
			.getAllItems(request, 0, 10);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getAllItems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.currentPageNumber").value(1))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.totalPages").value(diariesPage.getTotalPages()))
			.andExpect(jsonPath("$.data.totalItems").value(diariesPage.getTotalElements()));

		List<DiaryListDto> diaries = diariesPage.getContent();

		for (int i = 0; i < diaries.size(); i++) {
			DiaryListDto diary = diaries.get(i);

			resultActions
				.andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(diary.id()))
				.andExpect(jsonPath("$.data.items[%d].themeId".formatted(i)).value(diary.themeId()))
				.andExpect(jsonPath("$.data.items[%d].themeName".formatted(i)).value(diary.themeName()))
				.andExpect(jsonPath("$.data.items[%d].thumbnailUrl".formatted(i)).value(diary.thumbnailUrl()))
				.andExpect(jsonPath("$.data.items[%d].tags".formatted(i))
					.value(Matchers.containsInAnyOrder(diary.tags().toArray())))
				.andExpect(jsonPath("$.data.items[%d].storeName".formatted(i)).value(diary.storeName()))
				.andExpect(jsonPath("$.data.items[%d].escapeDate".formatted(i)).value(diary.escapeDate().toString()))
				.andExpect(jsonPath("$.data.items[%d].elapsedTime".formatted(i)).value(diary.elapsedTime()))
				.andExpect(jsonPath("$.data.items[%d].hintCount".formatted(i)).value(diary.hintCount()))
				.andExpect(jsonPath("$.data.items[%d].escapeResult".formatted(i)).value(diary.escapeResult()));
		}
	}

	@Test
	@DisplayName("""
		탈출일지 다건 조회, with 다중 필터 검색(
			강남, 홍대
			공포
			2024-03-20 ~ 2024-06-20
			성공, 노힌트
			방탈출 A
		)
		""")
	void t5_7() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/diaries/list")
				.content("""
					{
						"regionId": [1, 2],
						"tagNames": ["공포"],
						"startDate": "2024-03-20",
						"endDate": "2024-06-20",
						"isSuccess": "success",
						"isNoHint": true,
						"keyword": "방탈출 A"
					}
					""")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		DiaryFilterRequest request = DiaryFilterRequest.builder()
			.regionId(List.of(1L, 2L))
			.tagNames(List.of("공포"))
			.startDate(LocalDate.of(2024, 3, 20))
			.endDate(LocalDate.of(2024, 6, 20))
			.isSuccess("success")
			.isNoHint(true)
			.keyword("방탈출 A")
			.build();

		Page<DiaryListDto> diariesPage = diaryService
			.getAllItems(request, 0, 10);

		resultActions
			.andExpect(handler().handlerType(DiaryController.class))
			.andExpect(handler().methodName("getAllItems"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.currentPageNumber").value(1))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.totalPages").value(diariesPage.getTotalPages()))
			.andExpect(jsonPath("$.data.totalItems").value(diariesPage.getTotalElements()));

		List<DiaryListDto> diaries = diariesPage.getContent();

		for (int i = 0; i < diaries.size(); i++) {
			DiaryListDto diary = diaries.get(i);

			resultActions
				.andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(diary.id()))
				.andExpect(jsonPath("$.data.items[%d].themeId".formatted(i)).value(diary.themeId()))
				.andExpect(jsonPath("$.data.items[%d].themeName".formatted(i)).value(diary.themeName()))
				.andExpect(jsonPath("$.data.items[%d].thumbnailUrl".formatted(i)).value(diary.thumbnailUrl()))
				.andExpect(jsonPath("$.data.items[%d].tags".formatted(i))
					.value(Matchers.containsInAnyOrder(diary.tags().toArray())))
				.andExpect(jsonPath("$.data.items[%d].storeName".formatted(i)).value(diary.storeName()))
				.andExpect(jsonPath("$.data.items[%d].escapeDate".formatted(i)).value(diary.escapeDate().toString()))
				.andExpect(jsonPath("$.data.items[%d].elapsedTime".formatted(i)).value(diary.elapsedTime()))
				.andExpect(jsonPath("$.data.items[%d].hintCount".formatted(i)).value(diary.hintCount()))
				.andExpect(jsonPath("$.data.items[%d].escapeResult".formatted(i)).value(diary.escapeResult()));
		}
	}
}