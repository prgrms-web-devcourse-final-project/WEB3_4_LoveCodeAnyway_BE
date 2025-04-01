package com.ddobang.backend.domain.theme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.theme.entity.Theme;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long>, ThemeRepositoryCustom {
}
