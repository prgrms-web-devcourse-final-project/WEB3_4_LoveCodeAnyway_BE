package com.ddobang.backend.domain.diary.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.dto.response.DiaryDto;
import com.ddobang.backend.domain.diary.entity.Diary;
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
						"themeId": 1
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
			.andExpect(jsonPath("$.data.image").value(lastDiary.image()))
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
	@DisplayName("탈출일지 등록, theme id가 없을 때")
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
			.andExpect(jsonPath("$.errors[0].field").value("themeId"))
			.andExpect(jsonPath("$.errors[0].message").value("테마를 선택해주세요."));
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
			.andExpect(jsonPath("$.data.image").value(diary.getImageUrl()))
			.andExpect(jsonPath("$.data.escapeDate").value(diary.getEscapeDate().toString()))
			.andExpect(jsonPath("$.data.participants").value(diary.getParticipants()))
			.andExpect(jsonPath("$.data.difficulty").value(diary.getDiaryStats().getDifficulty()))
			.andExpect(jsonPath("$.data.fear").value(diary.getDiaryStats().getFear()))
			.andExpect(jsonPath("$.data.activity").value(diary.getDiaryStats().getActivity()))
			.andExpect(jsonPath("$.data.satisfaction").value(diary.getDiaryStats().getSatisfaction()))
			.andExpect(jsonPath("$.data.production").value(diary.getDiaryStats().getProduction()))
			.andExpect(jsonPath("$.data.story").value(diary.getDiaryStats().getStory()))
			.andExpect(jsonPath("$.data.question").value(diary.getDiaryStats().getQuestion()))
			.andExpect(jsonPath("$.data.interior").value(diary.getDiaryStats().getInterior()))
			.andExpect(jsonPath("$.data.deviceRatio").value(diary.getDiaryStats().getDeviceRatio()))
			.andExpect(jsonPath("$.data.hintCount").value(diary.getDiaryStats().getHintCount()))
			.andExpect(jsonPath("$.data.escapeResult").value(diary.getDiaryStats().isEscapeResult()))
			.andExpect(jsonPath("$.data.elapsedTime").value(diary.getDiaryStats().getElapsedTime()))
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
}
