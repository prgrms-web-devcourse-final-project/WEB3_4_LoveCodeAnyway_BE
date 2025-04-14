package com.ddobang.backend.domain.member.entity;

import java.math.BigDecimal;

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
import lombok.Setter;

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

	@Setter
	private String profilePictureUrl;

	private BigDecimal mannerScore;

	private Integer hostCount;

	private String admPassword;

	@Builder // 명시, 필요시 사용
	public Member(
		String nickname,
		Gender gender,
		String introduction,
		String kakaoId,
		String profilePictureUrl,
		BigDecimal mannerScore,
		Integer hostCount,
		String admPassword
	) {
		this.nickname = nickname;
		this.gender = gender;
		this.introduction = introduction;
		this.kakaoId = kakaoId;
		this.profilePictureUrl = profilePictureUrl;
		this.mannerScore = mannerScore;
		this.hostCount = hostCount;
		this.admPassword = admPassword;
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
	public static Member ofWithKakaoId(
		String nickname, Gender gender, String introduction, String imageUrl, String kakaoId
	) {
		return Member.builder()
			.nickname(nickname)
			.gender(gender)
			.introduction(introduction)
			.profilePictureUrl(imageUrl)
			.kakaoId(kakaoId)
			.build();
	}

	public Member(String nickname, String admPassword) {
		this.nickname = nickname;
		this.admPassword = admPassword;
	}

	public Member(Long id, String nickname) {
		this.id = id;
		this.nickname = nickname;
	}

	public void updateMannerScore(BigDecimal averageScore) {
		if (averageScore == null) {
			this.mannerScore = BigDecimal.valueOf(0);
			return;
		}
		this.mannerScore = averageScore;
	}

	// 프로필 정보 부분 수정
	public void updateProfile(String nickname, String introduction, String profileImageUrl) {
		if (nickname != null) {
			this.nickname = nickname;
		}
		if (introduction != null) {
			this.introduction = introduction;
		}
		if (profileImageUrl != null) {
			this.profilePictureUrl = profileImageUrl;
		}
	}
}
