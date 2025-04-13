package com.ddobang.backend.domain.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.dto.response.MemberStatResponse;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("MemberController 통합 테스트")
@Transactional
class MemberControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private MemberService memberService;

	@Test
	@DisplayName("사용자 분석 페이지 조회")
	@WithUserDetails(value = "testUser1")
	void t1() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/api/v1/members/stat"))
			.andDo(print());

		Member member = memberService.getByNickname("testUser1");
		MemberStatResponse memberStatResponse = memberService.getMemberStat(member);

		resultActions
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("getMemberStat"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("사용자 분석 데이터 조회 성공"))
			.andExpect(jsonPath("$.data.totalCount").value(memberStatResponse.totalCount()))
			.andExpect(jsonPath("$.data.successRate").value(memberStatResponse.successRate()))
			.andExpect(jsonPath("$.data.noHintSuccessCount").value(memberStatResponse.noHintSuccessCount()))
			.andExpect(jsonPath("$.data.noHintSuccessRate").value(memberStatResponse.noHintSuccessRate()))
			.andExpect(jsonPath("$.data.averageHintCount").value(memberStatResponse.averageHintCount()))
			.andExpect(jsonPath("$.data.genreCountMap").value(memberStatResponse.genreCountMap()))
			.andExpect(jsonPath("$.data.genreSuccessMap").value(memberStatResponse.genreSuccessMap()))
			.andExpect(jsonPath("$.data.tendencyMap.tendencyStimulating")
				.value(memberStatResponse.tendencyMap().get("tendencyStimulating")))
			.andExpect(jsonPath("$.data.tendencyMap.tendencyLogical")
				.value(memberStatResponse.tendencyMap().get("tendencyLogical")))
			.andExpect(jsonPath("$.data.tendencyMap.tendencyActive")
				.value(memberStatResponse.tendencyMap().get("tendencyActive")))
			.andExpect(jsonPath("$.data.tendencyMap.tendencySpatial")
				.value(memberStatResponse.tendencyMap().get("tendencySpatial")))
			.andExpect(jsonPath("$.data.tendencyMap.tendencyNarrative")
				.value(memberStatResponse.tendencyMap().get("tendencyNarrative")))
			.andExpect(jsonPath("$.data.monthlyCountMap").value(memberStatResponse.monthlyCountMap()))
			.andExpect(jsonPath("$.data.firstEscapeDate").value(memberStatResponse.firstEscapeDate().toString()))
			.andExpect(jsonPath("$.data.mostActiveMonth").value(memberStatResponse.mostActiveMonth()))
			.andExpect(jsonPath("$.data.mostActiveMonthCount").value(memberStatResponse.mostActiveMonthCount()))
			.andExpect(jsonPath("$.data.daysSinceFirstEscape").value(memberStatResponse.daysSinceFirstEscape()))
			.andExpect(jsonPath("$.data.lastMonthInfo.lastMonthCount")
				.value(memberStatResponse.lastMonthInfo().lastMonthCount()))
			.andExpect(jsonPath("$.data.lastMonthInfo.lastMonthAvgSatisfaction")
				.value(memberStatResponse.lastMonthInfo().lastMonthAvgSatisfaction()))
			.andExpect(jsonPath("$.data.lastMonthInfo.lastMonthAvgHintCount")
				.value(memberStatResponse.lastMonthInfo().lastMonthAvgHintCount()))
			.andExpect(jsonPath("$.data.lastMonthInfo.lastMonthSuccessRate")
				.value(memberStatResponse.lastMonthInfo().lastMonthSuccessRate()))
			.andExpect(jsonPath("$.data.lastMonthInfo.lastMonthAvgTime")
				.value(memberStatResponse.lastMonthInfo().lastMonthAvgTime()))
			.andExpect(jsonPath("$.data.lastMonthInfo.lastMonthTopTheme")
				.value(memberStatResponse.lastMonthInfo().lastMonthTopTheme()))
			.andExpect(jsonPath("$.data.lastMonthInfo.lastMonthTopSatisfaction")
				.value(memberStatResponse.lastMonthInfo().lastMonthTopSatisfaction()))
			.andExpect(jsonPath("$.data.difficultyHintAvgMap.1")
				.value(memberStatResponse.difficultyHintAvgMap().get(1)))
			.andExpect(jsonPath("$.data.difficultyHintAvgMap.2")
				.value(memberStatResponse.difficultyHintAvgMap().get(2)))
			.andExpect(jsonPath("$.data.difficultyHintAvgMap.3")
				.value(memberStatResponse.difficultyHintAvgMap().get(3)))
			.andExpect(jsonPath("$.data.difficultyHintAvgMap.4")
				.value(memberStatResponse.difficultyHintAvgMap().get(4)))
			.andExpect(jsonPath("$.data.difficultyHintAvgMap.5")
				.value(memberStatResponse.difficultyHintAvgMap().get(5)))
			.andExpect(jsonPath("$.data.difficultySatisAvgMap.1")
				.value(memberStatResponse.difficultySatisAvgMap().get(1)))
			.andExpect(jsonPath("$.data.difficultySatisAvgMap.2")
				.value(memberStatResponse.difficultySatisAvgMap().get(2)))
			.andExpect(jsonPath("$.data.difficultySatisAvgMap.3")
				.value(memberStatResponse.difficultySatisAvgMap().get(3)))
			.andExpect(jsonPath("$.data.difficultySatisAvgMap.4")
				.value(memberStatResponse.difficultySatisAvgMap().get(4)))
			.andExpect(jsonPath("$.data.difficultySatisAvgMap.5")
				.value(memberStatResponse.difficultySatisAvgMap().get(5)));
	}

	@Test
	@DisplayName("사용자 분석 페이지 조회, with 탈출일지가 없는 유저")
	@WithUserDetails(value = "testUser2")
	void t1_1() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/api/v1/members/stat"))
			.andDo(print());

		Member member = memberService.getByNickname("testUser2");
		MemberStatResponse memberStatResponse = memberService.getMemberStat(member);

		resultActions
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("getMemberStat"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("데이터가 없습니다. 탈출일지를 작성해주세요."))
			.andExpect(jsonPath("$.data").isEmpty());
	}
}
