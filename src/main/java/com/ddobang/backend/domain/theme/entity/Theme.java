package com.ddobang.backend.domain.theme.entity;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String name;

	private String description;

	private float officialDifficulty;

	private int runtime;

	private String recommendedParticipants;

	private int minParticipants;
	private int maxParticipants;

	private int price;

	private Status status;

	public enum Status {
		OPENED, CLOSED, INACTIVE, DELETED
	}

	private String reservationUrl;

	private String thumbnailUrl;

	@Builder
	public Theme(String name, String description, float officialDifficulty, int runtime, String recommendedParticipants,
		int minParticipants, int maxParticipants, int price, Status status, String reservationUrl,
		String thumbnailUrl) {
		this.name = name;
		this.description = description;
		this.officialDifficulty = officialDifficulty;
		this.runtime = runtime;
		this.recommendedParticipants = recommendedParticipants;
		this.minParticipants = minParticipants;
		this.maxParticipants = maxParticipants;
		this.price = price;
		this.status = status;
		this.reservationUrl = reservationUrl;
		this.thumbnailUrl = thumbnailUrl;
	}
}
