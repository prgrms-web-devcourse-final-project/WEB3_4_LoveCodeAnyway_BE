package com.ddobang.backend.global.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseTime {
	@CreatedDate
	@Setter(AccessLevel.PRIVATE)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Setter(AccessLevel.PRIVATE)
	private LocalDateTime modifiedAt;

	/**
	 * 테스트 전용 CreatedAt 강제 설정 메서드
	 * @param createdAt
	 */
	public void forceSetCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
