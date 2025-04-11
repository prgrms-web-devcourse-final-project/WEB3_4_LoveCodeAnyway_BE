package com.ddobang.backend.domain.member.entity;

import com.ddobang.backend.domain.member.entity.id.MemberTagMappingId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
public class MemberTagMapping {
	@EmbeddedId
	private MemberTagMappingId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("memberId")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("tagId")
	private MemberTag tag;

	public MemberTagMapping(Member member, MemberTag tag) {
		this.id = new MemberTagMappingId(member.getId(), tag.getId());
		this.member = member;
		this.tag = tag;
	}
}
