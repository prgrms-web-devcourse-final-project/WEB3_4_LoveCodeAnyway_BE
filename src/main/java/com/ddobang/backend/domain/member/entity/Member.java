package com.ddobang.backend.domain.member.entity;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String introduction;

	private String kakaoId;

	private String profilePictureUrl;

	private Integer mannerScore;

	private Integer hostCount;

	private String password; // 관리자용 비밀번호

	@Builder // 명시, 필요시 사용
	public Member(
		String nickname,
		Gender gender,
		String introduction,
		String kakaoId,
		String profilePictureUrl,
		Integer mannerScore,
		Integer hostCount,
		String password
	) {
		this.nickname = nickname;
		this.gender = gender;
		this.introduction = introduction;
		this.kakaoId = kakaoId;
		this.profilePictureUrl = profilePictureUrl;
		this.mannerScore = mannerScore;
		this.hostCount = hostCount;
		this.password = password;
	}

	public static Member of(String nickname, Gender gender, String introduction, String imageUrl) {
		return Member.builder()
			.nickname(nickname)
			.gender(gender)
			.introduction(introduction)
			.profilePictureUrl(imageUrl)
			.build();
	}

	// 신규 회원 가입 시 사용
	public static Member ofWithKakaoId(String nickname, Gender gender, String introduction, String imageUrl,
		String kakaoId) {
		return Member.builder()
			.nickname(nickname)
			.gender(gender)
			.introduction(introduction)
			.profilePictureUrl(imageUrl)
			.kakaoId(kakaoId)
			.build();
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	public Member(String nickname, String password) {
		this.nickname = nickname;
		this.password = password;
	}

	public Member(Long id, String nickname) {
		this.id = id;
		this.nickname = nickname;
	}
}
