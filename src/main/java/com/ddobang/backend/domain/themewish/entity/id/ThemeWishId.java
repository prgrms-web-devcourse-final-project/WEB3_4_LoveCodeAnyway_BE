package com.ddobang.backend.domain.themewish.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ThemeWishId
 * 모임 희망 테마 합성 키
 * @author 100minha
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ThemeWishId {
	@Column(name = "theme_id")
	private Long themeId;

	@Column(name = "member_id")
	private Long memberId;
}
