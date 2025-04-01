package com.ddobang.backend.domain.theme.entity;

import com.ddobang.backend.domain.theme.entity.id.ThemeTagMappingId;

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
 * ThemeTagMapping
 * 테마와 테마 테그의 매핑 테이블
 * @author 100minha
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeTagMapping {

	@EmbeddedId
	private ThemeTagMappingId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("themeId")
	@JoinColumn(name = "theme_id")
	private Theme theme;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("themeTagId")
	@JoinColumn(name = "theme_tag_id")
	private ThemeTag themeTag;

	public ThemeTagMapping(Theme theme, ThemeTag themeTag) {
		this.id = new ThemeTagMappingId(theme.getId(), themeTag.getId());
		this.theme = theme;
		this.themeTag = themeTag;
	}
}
