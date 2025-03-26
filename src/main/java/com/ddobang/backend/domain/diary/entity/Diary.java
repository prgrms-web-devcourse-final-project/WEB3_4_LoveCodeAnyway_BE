package com.ddobang.backend.domain.diary.entity;

import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Diary extends BaseTime {
    @Id
    Long id;
}
