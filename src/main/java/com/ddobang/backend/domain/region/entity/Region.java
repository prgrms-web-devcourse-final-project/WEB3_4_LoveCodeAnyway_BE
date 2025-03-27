package com.ddobang.backend.domain.region.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * region
 * 지역 엔티티
 * @author 100minha
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String majorRegion;

	String subRegion;

	@Builder
	public Region(String majorRegion, String subRegion) {
		this.majorRegion = majorRegion;
		this.subRegion = subRegion;
	}
}
