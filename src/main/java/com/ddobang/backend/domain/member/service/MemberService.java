package com.ddobang.backend.domain.member.service;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.Member;

@Service
public class MemberService {

	/**
	 * 사용자명으로 회원 조회
	 */
	public Member getMemberByUsername(String username) {
		// 테스트용 더미 Member 반환
		return Member.builder()
			.id(1L)
			//.username(username)
			.nickname("테스트사용자")
			//.email("test@example.com")
			.build();
	}

	/**
	 * ID로 회원 조회
	 */
	public Member getMemberById(Long id) {
		// 테스트용 더미 Member 반환
		return Member.builder()
			.id(id)
			//.username("user" + id)
			.nickname("사용자" + id)
			//.email("user" + id + "@example.com")
			.build();
	}
}