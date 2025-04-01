package com.ddobang.backend.domain.theme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.theme.entity.ThemeTag;

/**
 * ThemeTagRepository
 * @author 100minha
 */
@Repository
public interface ThemeTagRepository extends JpaRepository<ThemeTag, Long> {
}
