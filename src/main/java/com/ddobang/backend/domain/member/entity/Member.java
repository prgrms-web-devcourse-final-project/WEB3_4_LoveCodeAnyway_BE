package com.ddobang.backend.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 테스트용 Member 클래스
 * 실제 Member 클래스가 개발되기 전까지 임시로 사용
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String nickname;
	private String password;
	private String email;

	@Builder
	public Member(Long id, String username, String nickname, String password, String email) {
		this.id = id;
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.email = email;
	}
}