package com.ddobang.backend.domain.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.board.dto.request.AdminPostSearchCondition;
import com.ddobang.backend.domain.board.dto.response.PostSummaryResponse;
import com.ddobang.backend.domain.board.entity.QPost;
import com.ddobang.backend.domain.board.entity.QPostReply;
import com.ddobang.backend.domain.board.types.PostType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<PostSummaryResponse> findMyPosts(Long memberId, PostType type, String keyword, Pageable pageable) {
		QPost post = QPost.post;

		List<PostSummaryResponse> content = queryFactory
			.select(Projections.constructor(PostSummaryResponse.class,
				post.id,
				post.type,
				post.title,
				post.answered,
				post.createdAt
			))
			.from(post)
			.where(
				post.member.id.eq(memberId),
				post.deleted.isFalse(),
				eqType(type),
				containsKeyword(keyword, post)
			)
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(post.count())
			.from(post)
			.where(
				post.member.id.eq(memberId),
				post.deleted.isFalse(),
				eqType(type),
				containsKeyword(keyword, post)
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, count != null ? count : 0L);
	}

	@Override
	public Page<PostSummaryResponse> findPostsForAdmin(Pageable pageable, AdminPostSearchCondition condition) {
		QPost post = QPost.post;
		QPostReply postReply = QPostReply.postReply;

		List<PostSummaryResponse> content = queryFactory
			.select(Projections.constructor(PostSummaryResponse.class,
				post.id,
				post.type,
				post.title,
				post.answered,
				post.createdAt
			))
			.from(post)
			.leftJoin(post.replies, postReply)
			.where(
				eqType(condition.type()),
				eqAnswered(condition.answered()),
				eqDeleted(condition.deleted()),
				containsKeyword(condition.keyword(), post, postReply)
			)
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(post.countDistinct())
			.from(post)
			.leftJoin(post.replies, postReply)
			.where(
				eqType(condition.type()),
				eqAnswered(condition.answered()),
				eqDeleted(condition.deleted()),
				containsKeyword(condition.keyword(), post, postReply)
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, count != null ? count : 0L);
	}

	private BooleanExpression eqType(PostType type) {
		return type != null ? QPost.post.type.eq(type) : null;
	}

	private BooleanExpression eqAnswered(Boolean answered) {
		return answered != null ? QPost.post.answered.eq(answered) : null;
	}

	private BooleanExpression eqDeleted(Boolean deleted) {
		return deleted != null ? QPost.post.deleted.eq(deleted) : null;
	}

	private BooleanExpression containsKeyword(String keyword, QPost post) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		return post.title.containsIgnoreCase(keyword)
			.or(post.content.containsIgnoreCase(keyword));
	}

	private BooleanExpression containsKeyword(String keyword, QPost post, QPostReply reply) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		return post.title.containsIgnoreCase(keyword)
			.or(post.content.containsIgnoreCase(keyword))
			.or(post.member.nickname.containsIgnoreCase(keyword))
			.or(reply.content.containsIgnoreCase(keyword));
	}
}

