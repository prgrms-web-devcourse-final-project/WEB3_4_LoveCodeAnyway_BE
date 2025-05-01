package com.ddobang.backend.domain.themewish.entity;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.themewish.entity.id.ThemeWishId;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ThemeWish
 * 모임 희망 테마 엔티티
 * @author 100minha
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeWish extends BaseTime {

	@EmbeddedId
	private ThemeWishId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("themeId")
	@JoinColumn(name = "theme_id")
	private Theme theme;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("memberId")
	@JoinColumn(name = "member_id")
	private Member member;

	public ThemeWish(Theme theme, Member member) {
		this.id = new ThemeWishId(theme.getId(), member.getId());
		this.theme = theme;
		this.member = member;
	}
}
