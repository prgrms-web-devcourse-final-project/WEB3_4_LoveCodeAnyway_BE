package com.ddobang.backend.domain.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.dto.request.UpdateTagsRequest;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberTag;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.member.repository.MemberStatRepository;
import com.ddobang.backend.domain.member.repository.MemberTagMappingRepository;
import com.ddobang.backend.domain.member.repository.MemberTagRepository;
import com.ddobang.backend.global.security.jwt.JwtTokenProvider;
import com.ddobang.backend.global.security.jwt.JwtTokenType;
import com.ddobang.backend.support.MemberTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@DisplayName("MemberController 테스트")
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberTagRepository tagRepository;
	@Autowired
	private MemberTagMappingRepository mappingRepository;
	@Autowired
	private MemberStatRepository statRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("[성공] 닉네임 중복 여부를 확인할 수 있다")
	void checkNicknameDuplicate_success() throws Exception {
		mockMvc.perform(get("/api/v1/members/check-nickname")
				.param("nickname", "방탈출왕"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value(true));
	}

	@Test
	@DisplayName("[성공] 내 프로필 정보를 조회할 수 있다")
	void getMyBasicProfile_success() throws Exception {
		Member member = memberRepository.save(MemberTestFactory.Basic());
		String token = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		mockMvc.perform(get("/api/v1/members/me")
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.nickname").value(member.getNickname()))
			.andExpect(jsonPath("$.data.gender").value(member.getGender().toString()))
			.andExpect(jsonPath("$.data.introduction").value(member.getIntroduction()))
			.andExpect(jsonPath("$.data.profilePictureUrl").value(member.getProfilePictureUrl()))
			.andExpect(jsonPath("$.data.mannerScore").value(member.getMannerScore()));
	}

	@Test
	@DisplayName("[성공] 내 프로필 정보를 수정할 수 있다")
	void updateMyProfile_success() throws Exception {
		Member member = memberRepository.save(MemberTestFactory.Basic());
		String token = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		String body = """
				{
				  "nickname": "변경닉네임",
				  "introduction": "변경자기소개",
				  "profileImageUrl": "https://update.img"
				}
			""";

		mockMvc.perform(patch("/api/v1/members/me")
				.cookie(new Cookie("accessToken", token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.nickname").value("변경닉네임"))
			.andExpect(jsonPath("$.data.introduction").value("변경자기소개"))
			.andExpect(jsonPath("$.data.profilePictureUrl").value("https://update.img"));
	}

	@Test
	@DisplayName("[성공] 내 사용자 태그 목록을 조회할 수 있다")
	void getMyTags_success() throws Exception {
		Member member = memberRepository.save(MemberTestFactory.fullWithTagsAndStats(
			memberRepository, tagRepository, mappingRepository, statRepository
		));
		String token = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		mockMvc.perform(get("/api/v1/members/me/tags")
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(5)); // 태그 5개
	}

	@Test
	@DisplayName("[성공] 내 사용자 태그를 수정할 수 있다")
	void updateMyTags_success() throws Exception {
		Member member = MemberTestFactory.fullWithTagsAndStats(
			memberRepository, tagRepository, mappingRepository, statRepository);

		List<MemberTag> newTags = List.of(
			tagRepository.save(new MemberTag("분위기 메이커에요")),
			tagRepository.save(new MemberTag("서사에 몰입해요"))
		);
		List<Long> newTagIds = newTags.stream().map(MemberTag::getId).toList();

		String token = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		UpdateTagsRequest request = new UpdateTagsRequest(newTagIds); // 태그 ID 리스트 생성

		mockMvc.perform(patch("/api/v1/members/me/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("[성공] 내 요약 통계를 조회할 수 있다")
	void getMyStats_success() throws Exception {
		Member member = MemberTestFactory.fullWithTagsAndStats(
			memberRepository, tagRepository, mappingRepository, statRepository);

		String token = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		mockMvc.perform(get("/api/v1/members/me/stats")
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalCount").value(50))
			.andExpect(jsonPath("$.data.successRate").value(0.75))
			.andExpect(jsonPath("$.data.noHintSuccessRate").value(0.65));
	}

	@Test
	@DisplayName("[성공] 타인 프로필을 정상적으로 조회할 수 있다")
	void getOtherProfile_success() throws Exception {
		Member member = MemberTestFactory.fullWithTagsAndStats(
			memberRepository, tagRepository, mappingRepository, statRepository);

		String token = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		mockMvc.perform(get("/api/v1/members/{memberId}/profile", member.getId())
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.profile.nickname").value(member.getNickname())) // 닉네임
			.andExpect(jsonPath("$.data.tags").isArray()) // 태그
			.andExpect(jsonPath("$.data.stats.totalCount").value(50)); // 통계
	}
}
