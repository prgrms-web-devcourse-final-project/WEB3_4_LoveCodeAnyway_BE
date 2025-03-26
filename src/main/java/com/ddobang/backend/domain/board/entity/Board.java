package com.ddobang.backend.domain.board.entity;

import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Board extends BaseTime {
    @Id
    Long id;
}
