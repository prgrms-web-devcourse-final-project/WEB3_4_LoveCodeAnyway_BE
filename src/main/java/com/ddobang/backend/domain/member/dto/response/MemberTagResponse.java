package com.ddobang.backend.domain.member.dto.response;

import com.ddobang.backend.domain.member.entity.MemberTag;

public record MemberTagResponse(
	Long id,
	String name
) {
	public static MemberTagResponse from(MemberTag tag) {
		return new MemberTagResponse(
			tag.getId(),
			tag.getName()
		);
	}
}
