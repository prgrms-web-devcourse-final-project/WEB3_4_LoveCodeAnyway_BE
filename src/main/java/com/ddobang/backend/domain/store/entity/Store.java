package com.ddobang.backend.domain.store.entity;

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.dto.StoreRequest;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String name;

	private String address;

	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private Status status;

	public enum Status {
		OPENED, CLOSED, INACTIVE, DELETED
	}

	@ManyToOne(fetch = FetchType.LAZY)
	private Region region;

	@Builder
	public Store(String name, String address, String phoneNumber, Status status, Region region) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.status = status;
		this.region = region;
	}

	public static Store of(StoreRequest storeRequest, Region region) {
		return Store.builder()
			.name(storeRequest.name())
			.address(storeRequest.address())
			.phoneNumber(storeRequest.phoneNumber())
			.status(Store.Status.valueOf(storeRequest.status()))
			.region(region)
			.build();
	}

	public void modify(StoreRequest storeRequest, Region region) {
		this.name = storeRequest.name();
		this.address = storeRequest.address();
		this.phoneNumber = storeRequest.phoneNumber();
		this.status = Store.Status.valueOf(storeRequest.status());
		this.region = region;
	}

	public void delete() {
		this.status = Status.DELETED;
	}
}
