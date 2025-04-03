package com.ddobang.backend.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// // fot test
// @AllArgsConstructor
// @Builder
// //여기까지
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickname;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String introduction;

	private String kakaoId;

	private String profilePictureUrl;

	private Integer mannerScore;

	private Integer hostCount;

	private String password; // 관리자용 비밀번호

	@ManyToMany
	@JoinTable(
		name = "member_tag_mapping",
		joinColumns = @JoinColumn(name = "member_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private List<MemberTag> tags = new ArrayList<>();

	// @ManyToMany
	// @JoinTable(
	// 	name = "member_theme_mapping",
	// 	joinColumns = @JoinColumn(name = "member_id"),
	// 	inverseJoinColumns = @JoinColumn(name = "theme_id")
	// )
	// private List<Theme> themes = new ArrayList<>();

	@Builder // 명시, 필요시 사용
	public Member(
		String nickname,
		Gender gender,
		String introduction,
		String kakaoId,
		String profilePictureUrl,
		Integer mannerScore,
		Integer hostCount,
		String password,
		List<MemberTag> tags
	) {
		this.nickname = nickname;
		this.gender = gender;
		this.introduction = introduction;
		this.kakaoId = kakaoId;
		this.profilePictureUrl = profilePictureUrl;
		this.mannerScore = mannerScore;
		this.hostCount = hostCount;
		this.password = password;
		this.tags = tags != null ? tags : new ArrayList<>();
	}
}
