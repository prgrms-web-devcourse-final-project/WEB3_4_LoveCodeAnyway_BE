package com.ddobang.backend.domain.member.entity.id;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class MemberTagMappingId implements Serializable {

	private Long memberId;
	private Long tagId;

	public MemberTagMappingId(Long memberId, Long tagId) {
		this.memberId = memberId;
		this.tagId = tagId;
	}
}

