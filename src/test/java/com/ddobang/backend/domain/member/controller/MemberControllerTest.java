package com.ddobang.backend.domain.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.dto.request.ProfileRequest;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
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
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("[성공] 나의 기본 프로필을 정상적으로 조회할 수 있다.")
	void getMyBasicProfile_success() throws Exception {
		// given
		Member member = memberRepository.save(MemberTestFactory.full());
		String token = jwtTokenProvider.generateToken(member, JwtTokenType.ACCESS, false);

		// when & then
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
	@DisplayName("[성공] 타인 프로필을 정상적으로 조회할 수 있다")
	void getOtherProfile_success() throws Exception {
		// given
		Member target = memberRepository.save(MemberTestFactory.withNickname("오애순"));
		Member requester = memberRepository.save(MemberTestFactory.withNickname("또방이"));
		String token = jwtTokenProvider.generateToken(requester, JwtTokenType.ACCESS, false);

		ProfileRequest request = new ProfileRequest(target.getId());

		// when & then
		mockMvc.perform(post("/api/v1/members/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.nickname").value(target.getNickname()));
	}

	@Test
	@DisplayName("[실패] 존재하지 않는 사용자의 경우 예외가 발생한다")
	void getOtherProfile_notFound() throws Exception {
		// given
		Member requester = memberRepository.save(MemberTestFactory.full());
		String token = jwtTokenProvider.generateToken(requester, JwtTokenType.ACCESS, false);
		ProfileRequest request = new ProfileRequest(9999L); // 없는 ID

		// when & then
		mockMvc.perform(post("/api/v1/members/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isNotFound());
	}
}
