package com.ddobang.backend.domain.theme.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ThemeTagMappingId
 * 테마와 테그의 매핑 테이블에서 합성키로 사용할 클래스
 * @author 100minha
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ThemeTagMappingId {
	@Column(name = "theme_id")
	private Long themeId;

	@Column(name = "theme_tag_id")
	private Long themeTagId;
}
