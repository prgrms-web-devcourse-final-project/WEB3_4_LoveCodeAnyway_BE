package com.ddobang.backend.support;

import java.math.BigDecimal;

import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;

public class MemberTestFactory {

	// 기본 테스트 멤버 생성 (일반 사용자)
	public static Member full() {
		return Member.builder()
			.nickname("테스트닉네임")
			.gender(Gender.BLIND)
			.introduction("자기소개 예시입니다.")
			.kakaoId("kakao1234")
			.profilePictureUrl("https://example.com/profile.jpg")
			.mannerScore(BigDecimal.valueOf(50))
			.hostCount(50)
			.password(null)
			.build();
	}

	// 카카오ID만 주입
	public static Member withKakaoId(String kakaoId) {
		return Member.builder()
			.nickname("테스트닉네임")
			.gender(Gender.BLIND)
			.introduction("자기소개 예시입니다.")
			.kakaoId(kakaoId)
			.profilePictureUrl("https://example.com/profile.jpg")
			.mannerScore(BigDecimal.valueOf(50))
			.hostCount(50)
			.password(null)
			.build();
	}

	// 닉네임만 주입
	public static Member withNickname(String nickname) {
		return Member.builder()
			.nickname(nickname)
			.gender(Gender.BLIND)
			.introduction("자기소개 예시입니다.")
			.kakaoId("kakao1234")
			.profilePictureUrl("https://example.com/profile.jpg")
			.mannerScore(BigDecimal.valueOf(50))
			.hostCount(50)
			.password(null)
			.build();
	}

	// 관리자 멤버 생성
	public static Member admin() {
		return Member.builder()
			.nickname("관리자")
			.gender(Gender.MALE)
			.introduction("관리자 소개입니다.")
			.kakaoId("adminKakaoId")
			.profilePictureUrl("https://example.com/admin.jpg")
			.mannerScore(BigDecimal.ZERO)
			.hostCount(0)
			.password("encodedPassword")
			.build();
	}
}
