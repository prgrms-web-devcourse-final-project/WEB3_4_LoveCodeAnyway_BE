package com.ddobang.backend.domain.member.entity;

import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class MemberReview extends BaseTime {
    @Id
    Long id;
}
