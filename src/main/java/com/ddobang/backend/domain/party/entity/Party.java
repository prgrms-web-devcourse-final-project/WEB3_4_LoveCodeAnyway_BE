package com.ddobang.backend.domain.party.entity;

import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Party extends BaseTime {
    @Id
    Long id;
}
