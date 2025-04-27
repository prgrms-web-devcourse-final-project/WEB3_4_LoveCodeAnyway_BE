package com.ddobang.backend.domain.themewish.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.themewish.entity.ThemeWish;
import com.ddobang.backend.domain.themewish.entity.id.ThemeWishId;
import com.ddobang.backend.domain.themewish.exception.ThemeWishErrorCode;
import com.ddobang.backend.domain.themewish.exception.ThemeWishException;
import com.ddobang.backend.domain.themewish.repository.ThemeWishRepository;

import lombok.RequiredArgsConstructor;

/**
 * ThemeWishService
 * <p></p>
 * @author 100minha
 */
@Service
@RequiredArgsConstructor
public class ThemeWishService {
	private final ThemeWishRepository themeWishRepository;

	@Transactional
	public void addThemeWish(Theme theme, Member member) {
		if (themeWishRepository.countThemeWishByMember(member) >= 10) {
			throw new ThemeWishException(ThemeWishErrorCode.EXCEEDED_MAXIMUM_WISHES);
		}
		if (themeWishRepository.existsThemeWishByThemeAndMember(theme, member)) {
			throw new ThemeWishException(ThemeWishErrorCode.ALREADY_WISHED);
		}
		themeWishRepository.save(new ThemeWish(theme, member));
	}

	@Transactional(readOnly = true)
	public List<ThemeWish> getThemeWishesByMemberId(Long memberId) {
		return themeWishRepository.findThemesWishedByMemberId(memberId);

	}

	@Transactional
	public void deleteThemeWish(ThemeWishId id) {
		themeWishRepository.deleteById(id);
	}
}
