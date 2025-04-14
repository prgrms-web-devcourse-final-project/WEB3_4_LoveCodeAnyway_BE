package com.ddobang.backend.domain.member.repository;

import static com.ddobang.backend.domain.member.entity.QMember.*;
import static com.ddobang.backend.domain.member.entity.QMemberTag.*;
import static com.ddobang.backend.domain.member.entity.QMemberTagMapping.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.member.entity.MemberTagMapping;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberTagMappingQueryRepositoryImpl implements MemberTagMappingQueryRepository {

	private final JPAQueryFactory query;

	@Override
	public List<MemberTagMapping> findAllByMemberId(Long memberId) {
		return query.selectFrom(memberTagMapping)
			.join(memberTagMapping.member, member).fetchJoin()
			.join(memberTagMapping.tag, memberTag).fetchJoin()
			.where(member.id.eq(memberId))
			.fetch();
	}
}
