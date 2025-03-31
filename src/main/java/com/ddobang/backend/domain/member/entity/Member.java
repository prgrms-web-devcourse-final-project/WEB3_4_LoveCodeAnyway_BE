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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickname;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String introduction;

	private Long kakaoId;

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
}
