package com.ddobang.backend.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.diary.entity.DiaryStat;

@Repository
public interface DiaryStatRepository extends JpaRepository<DiaryStat, Long> {
}
