package com.ddobang.backend.domain.theme.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.dto.request.ThemeForAdminRequest;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@Length(max = 100)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@PositiveOrZero
	@Max(5)
	private float officialDifficulty;

	private int runtime;

	@PositiveOrZero
	@Max(8)
	private int minParticipants;
	@PositiveOrZero
	@Max(20)
	private int maxParticipants;
	@PositiveOrZero
	@Max(9_999_999)
	private int price;

	@Enumerated(EnumType.STRING)
	@NotNull
	private Status status;

	public enum Status {
		OPENED, CLOSED, INACTIVE, DELETED
	}

	private String reservationUrl;

	private String thumbnailUrl;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ThemeTagMapping> themeTagMappings = new ArrayList<>();

	@Builder
	public Theme(String name, String description, float officialDifficulty, int runtime,
		int minParticipants, int maxParticipants, int price, Status status, String reservationUrl,
		String thumbnailUrl, Store store, List<ThemeTag> themeTags) {
		this.name = name;
		this.description = description;
		this.officialDifficulty = officialDifficulty;
		this.runtime = runtime;
		this.minParticipants = minParticipants;
		this.maxParticipants = maxParticipants;
		this.price = price;
		this.status = status;
		this.reservationUrl = reservationUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.store = store;

		if (themeTags != null) {
			themeTags.forEach(themeTag ->
				themeTagMappings.add(new ThemeTagMapping(this, themeTag)));
		}
	}

	public static Theme of(ThemeForAdminRequest request, Store store, List<ThemeTag> themeTags) {
		return Theme.builder()
			.name(request.name())
			.description(request.description())
			.officialDifficulty(request.officialDifficulty())
			.runtime(request.runtime())
			.minParticipants(request.minParticipants())
			.maxParticipants(request.maxParticipants())
			.price(request.price())
			.status(Status.valueOf(request.status()))
			.reservationUrl(request.reservationUrl())
			.thumbnailUrl(request.thumbnailUrl())
			.store(store)
			.themeTags(themeTags)
			.build();
	}

	public void modify(ThemeForAdminRequest request, Store store, List<ThemeTag> themeTags) {
		this.name = request.name();
		this.description = request.description();
		this.officialDifficulty = request.officialDifficulty();
		this.runtime = request.runtime();
		this.minParticipants = request.minParticipants();
		this.maxParticipants = request.maxParticipants();
		this.price = request.price();
		this.status = Status.valueOf(request.status());
		this.reservationUrl = request.reservationUrl();
		this.thumbnailUrl = request.thumbnailUrl();
		this.store = store;

		if (themeTags != null) {
			this.themeTagMappings.clear();
			themeTags.forEach(themeTag ->
				themeTagMappings.add(new ThemeTagMapping(this, themeTag)));
		}
	}

	public void delete() {
		this.status = Status.DELETED;
	}
}
