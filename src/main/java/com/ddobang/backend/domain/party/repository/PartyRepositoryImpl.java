package com.ddobang.backend.domain.party.repository;

import static com.ddobang.backend.domain.party.types.PartyStatus.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.QMember;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.MyJoinedPartySummaryResponse;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.entity.QParty;
import com.ddobang.backend.domain.party.entity.QPartyMember;
import com.ddobang.backend.domain.party.entity.QPartyMemberReview;
import com.ddobang.backend.domain.party.types.PartyMemberRole;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.party.types.PartyTodoFilter;
import com.ddobang.backend.domain.store.entity.QStore;
import com.ddobang.backend.domain.theme.entity.QTheme;
import com.ddobang.backend.domain.theme.entity.QThemeTagMapping;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.tag.entity.QThemeTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartyRepositoryImpl implements PartyRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<PartySummaryResponse> getParties(Long lastId, int size, PartySearchCondition condition) {
		QParty party = QParty.party;
		QTheme theme = QTheme.theme;
		QStore store = QStore.store;
		QPartyMember pm = QPartyMember.partyMember;
		QMember host = QMember.member;
		QThemeTagMapping mapping = QThemeTagMapping.themeTagMapping;
		QThemeTag themeTag = QThemeTag.themeTag;

		return queryFactory
			.select(Projections.constructor(PartySummaryResponse.class,
				party.id,
				party.title,
				party.scheduledAt,

				party.totalParticipants
					.subtract(party.participantsNeeded)
					.add(party.acceptedParticipantsCount),
				party.totalParticipants,
				party.rookieAvailable,

				store.name,

				theme.id,
				theme.name,
				theme.thumbnailUrl,

				host.id,
				host.nickname,
				host.profilePictureUrl
			))
			.from(party)
			.join(party.theme, theme)
			.join(theme.store, store)
			.join(party.partyMembers, pm)
			.join(pm.member, host)
			.leftJoin(theme.themeTagMappings, mapping)
			.leftJoin(mapping.themeTag, themeTag)
			.where(
				party.status.in(RECRUITING, FULL),
				party.deleted.eq(false),
				pm.role.eq(PartyMemberRole.HOST),
				keywordContains(condition.keyword(), party, theme, store, host),
				regionIn(condition.regionIds(), store),
				dateIn(condition.dates(), party),
				tagIn(condition.tagsIds(), themeTag),
				ltLastId(lastId, party)
			)
			.orderBy(party.id.desc())
			.limit(size)
			.distinct()
			.fetch();
	}

	private BooleanExpression keywordContains(String keyword, QParty party, QTheme theme, QStore store, QMember host) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}

		return party.title.containsIgnoreCase(keyword)
			.or(theme.name.containsIgnoreCase(keyword))
			.or(store.name.containsIgnoreCase(keyword))
			.or(host.nickname.containsIgnoreCase(keyword));
	}

	private BooleanExpression regionIn(List<Long> regionIds, QStore store) {
		if (regionIds == null || regionIds.isEmpty()) {
			return null;
		}
		return store.region.id.in(regionIds);
	}

	private BooleanExpression dateIn(List<LocalDate> dates, QParty party) {
		if (dates == null || dates.isEmpty()) {
			return null;
		}

		return dates.stream()
			.map(date -> party.scheduledAt.between(
				date.atStartOfDay(), date.plusDays(1).atStartOfDay().minusNanos(1)))
			.reduce(BooleanExpression::or)
			.orElse(null);
	}

	private BooleanExpression tagIn(List<Long> tagIds, QThemeTag themeTag) {
		if (tagIds == null || tagIds.isEmpty()) {
			return null;
		}
		return themeTag.id.in(tagIds);
	}

	private BooleanExpression ltLastId(Long lastId, QParty party) {
		return lastId != null ? party.id.lt(lastId) : null;
	}

	@Override
	public Page<PartySummaryResponse> findOtherMemberJoinedParties(Member member, Pageable pageable) {
		QParty party = QParty.party;
		QPartyMember pm = QPartyMember.partyMember;
		QPartyMember hostPm = new QPartyMember("hostPm");
		QMember host = new QMember("host");
		QStore store = QStore.store;
		QTheme theme = QTheme.theme;

		BooleanBuilder builder = new BooleanBuilder();

		builder.and(getCompletedAndAcceptedCondition(member, party, pm));

		List<PartySummaryResponse> content = queryFactory
			.selectDistinct(Projections.constructor(PartySummaryResponse.class,
				party.id,
				party.title,
				party.scheduledAt,
				party.totalParticipants
					.subtract(party.participantsNeeded)
					.add(party.acceptedParticipantsCount),
				party.totalParticipants,
				party.rookieAvailable,
				store.name,
				theme.id,
				theme.name,
				theme.thumbnailUrl,
				host.id,
				host.nickname,
				host.profilePictureUrl
			))
			.from(party)
			.join(party.partyMembers, pm)
			.join(party.partyMembers, hostPm).on(hostPm.role.eq(PartyMemberRole.HOST))
			.join(hostPm.member, host)
			.join(party.theme, theme)
			.join(theme.store, store)
			.where(builder)
			.orderBy(party.scheduledAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(party.id.countDistinct())
			.from(party)
			.join(party.partyMembers, pm)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, count != null ? count : 0L);
	}

	private BooleanExpression getCompletedAndAcceptedCondition(Member member, QParty party, QPartyMember pm) {
		return party.scheduledAt.before(LocalDateTime.now())
			.and(party.status.eq(PartyStatus.COMPLETED))
			.and(pm.status.eq(PartyMemberStatus.ACCEPTED))
			.and(pm.member.eq(member));
	}

	@Override
	public Page<MyJoinedPartySummaryResponse> findMyPartyHistories(
		Member member,
		PartyMemberRole role,
		PartyTodoFilter todoFilter,
		Pageable pageable
	) {
		QParty party = QParty.party;
		QPartyMember pm = QPartyMember.partyMember;
		QPartyMember hostPm = new QPartyMember("hostPm");
		QStore store = QStore.store;
		QTheme theme = QTheme.theme;
		QMember myMember = QMember.member;
		QMember host = new QMember("host");
		QPartyMemberReview review = QPartyMemberReview.partyMemberReview;

		BooleanBuilder baseCondition = new BooleanBuilder();
		baseCondition.or(getCompletedAndAcceptedCondition(member, party, pm))
			.or(getFullAndAcceptedCondition(member, party, pm))
			.or(getRecruitingAndAcceptedOrApplicantCondition(member, party, pm))
			.or(getHostedPartyCondition(member, pm));

		BooleanBuilder filterCondition = new BooleanBuilder();
		if (role != null) {
			filterCondition.and(pm.role.eq(role));
		}
		if (todoFilter == PartyTodoFilter.HOST_PENDING) {
			filterCondition.and(party.status.eq(PartyStatus.PENDING));
		} else if (todoFilter == PartyTodoFilter.PARTICIPANT_REVIEW) {
			filterCondition.and(party.status.eq(PartyStatus.COMPLETED));
			filterCondition.and(
				JPAExpressions.selectOne()
					.from(review)
					.where(
						review.party.id.eq(party.id),
						review.reviewer.id.eq(member.getId())
					)
					.notExists()
			);
		}

		BooleanBuilder finalCondition = new BooleanBuilder();
		finalCondition.and(baseCondition).and(filterCondition);

		List<MyJoinedPartySummaryResponse> content = queryFactory
			.select(Projections.constructor(MyJoinedPartySummaryResponse.class,
				party.id,
				party.title,
				party.scheduledAt,
				party.totalParticipants
					.subtract(party.participantsNeeded)
					.add(party.acceptedParticipantsCount),
				party.totalParticipants,
				party.rookieAvailable,
				store.name,
				theme.id,
				theme.name,
				theme.thumbnailUrl,
				host.id,
				host.nickname,
				host.profilePictureUrl,
				pm.role,
				JPAExpressions
					.selectOne()
					.from(review)
					.where(
						review.party.id.eq(party.id),
						review.reviewer.id.eq(member.getId())
					)
					.exists(),
				party.status
			))
			.from(pm)
			.join(pm.party, party)
			.join(party.theme, theme)
			.join(theme.store, store)
			.join(pm.member, myMember)
			.join(party.partyMembers, hostPm)
			.join(hostPm.member, host)
			.where(
				getHostPmCondition(hostPm),
				finalCondition
			)
			.orderBy(party.scheduledAt.desc())
			.distinct()
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(party.countDistinct())
			.from(pm)
			.join(pm.party, party)
			.join(party.partyMembers, hostPm)
			.where(
				getHostPmCondition(hostPm),
				finalCondition
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, count != null ? count : 0L);
	}

	private BooleanExpression getFullAndAcceptedCondition(Member member, QParty party, QPartyMember pm) {
		return party.scheduledAt.after(LocalDateTime.now())
			.and(party.status.eq(PartyStatus.FULL))
			.and(pm.status.eq(PartyMemberStatus.ACCEPTED))
			.and(pm.member.eq(member));
	}

	private BooleanExpression getRecruitingAndAcceptedOrApplicantCondition(Member member, QParty party,
		QPartyMember pm) {
		return party.scheduledAt.after(LocalDateTime.now())
			.and(party.status.eq(PartyStatus.RECRUITING))
			.and(pm.status.in(PartyMemberStatus.ACCEPTED, PartyMemberStatus.APPLICANT))
			.and(pm.member.eq(member));
	}

	private BooleanExpression getHostedPartyCondition(Member member, QPartyMember pm) {
		return pm.role.eq(PartyMemberRole.HOST)
			.and(pm.member.eq(member));
	}

	private BooleanExpression getHostPmCondition(QPartyMember hostPm) {
		return hostPm.role.eq(PartyMemberRole.HOST);
	}

	@Override
	public List<PartySummaryResponse> getPartiesByTheme(Theme theme, Long lastId, int size) {
		QParty party = QParty.party;
		QStore store = QStore.store;
		QTheme qTheme = QTheme.theme;
		QPartyMember pm = QPartyMember.partyMember;
		QMember host = QMember.member;

		return queryFactory
			.select(Projections.constructor(PartySummaryResponse.class,
				party.id,
				party.title,
				party.scheduledAt,

				party.totalParticipants
					.subtract(party.participantsNeeded)
					.add(party.acceptedParticipantsCount),

				party.totalParticipants,
				party.rookieAvailable,

				store.name,

				qTheme.id,
				qTheme.name,
				qTheme.thumbnailUrl,

				host.id,
				host.nickname,
				host.profilePictureUrl
			))
			.from(party)
			.join(party.theme, qTheme)
			.join(qTheme.store, store)

			.join(party.partyMembers, pm)
			.join(pm.member, host)
			.where(
				party.theme.eq(theme),
				party.status.eq(PartyStatus.RECRUITING),
				party.deleted.eq(false),
				pm.role.eq(PartyMemberRole.HOST),
				ltLastId(lastId, party)
			)
			.orderBy(party.id.desc())
			.limit(size)
			.fetch();
	}
}
