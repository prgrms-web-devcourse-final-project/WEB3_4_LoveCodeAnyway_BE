package com.ddobang.backend.domain.message.entity;

import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Message extends BaseTime {
    @Id
    Long id;
}
