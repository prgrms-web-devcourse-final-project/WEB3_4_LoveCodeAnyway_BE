package com.ddobang.backend.domain.theme.entity;

import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ThemeReview extends BaseTime {
    @Id
    Long id;
}
