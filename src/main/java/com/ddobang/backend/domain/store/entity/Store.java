package com.ddobang.backend.domain.store.entity;

import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Store extends BaseTime {
    @Id
    Long id;
}
