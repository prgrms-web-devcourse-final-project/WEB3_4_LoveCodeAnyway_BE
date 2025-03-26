package com.ddobang.backend.domain.notification.entity;

import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Notification extends BaseTime {
    @Id
    Long id;
}
