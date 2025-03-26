package com.ddobang.backend.domain.store.entity;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
}
