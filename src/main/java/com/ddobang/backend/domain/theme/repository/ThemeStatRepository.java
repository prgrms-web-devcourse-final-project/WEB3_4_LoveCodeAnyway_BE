package com.ddobang.backend.domain.theme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.theme.entity.ThemeStat;

/**
 * ThemeStatRepository
 * @author 100minha
 */
@Repository
public interface ThemeStatRepository extends JpaRepository<ThemeStat, Long> {
}
