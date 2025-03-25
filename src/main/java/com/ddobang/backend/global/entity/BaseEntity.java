package com.ddobang.backend.global.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    @EqualsAndHashCode.Include
    private Long id;
}
