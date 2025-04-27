package com.ddobang.backend.domain.themewish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.themewish.entity.ThemeWish;
import com.ddobang.backend.domain.themewish.entity.id.ThemeWishId;

/**
 * ThemeWishRepository
 * <p></p>
 * @author 100minha
 */
@Repository
public interface ThemeWishRepository extends JpaRepository<ThemeWish, ThemeWishId>, ThemeWishRepositoryCustom {

	int countThemeWishByMember(Member member);

	boolean existsThemeWishByThemeAndMember(Theme theme, Member member);
}
