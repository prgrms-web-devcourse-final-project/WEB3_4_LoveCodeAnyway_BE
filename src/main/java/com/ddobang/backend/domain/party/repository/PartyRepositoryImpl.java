package com.ddobang.backend.domain.party.repository;

import com.ddobang.backend.domain.member.entity.QMember;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.entity.QParty;
import com.ddobang.backend.domain.party.entity.QPartyMember;
import com.ddobang.backend.domain.store.entity.QStore;
import com.ddobang.backend.domain.theme.entity.QTheme;
import com.ddobang.backend.domain.theme.entity.QThemeTag;
import com.ddobang.backend.domain.theme.entity.QThemeTagMapping;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.ddobang.backend.domain.party.types.PartyMemberRole.HOST;
import static com.ddobang.backend.domain.party.types.PartyStatus.FULL;
import static com.ddobang.backend.domain.party.types.PartyStatus.RECRUITING;

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

						party.participantsNeeded.subtract(party.acceptedParticipantsCount),
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
						pm.role.eq(HOST),
						keywordContains(condition.keyword(), party, theme, store, host),
						regionIn(condition.regionIds(), store),
						dateIn(condition.dates(), party),
						tagIn(condition.tags(), themeTag),
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

	private BooleanExpression tagIn(List<String> tags, QThemeTag themeTag) {
		if (tags == null || tags.isEmpty()) {
			return null;
		}
		return themeTag.name.in(tags);
	}

	private BooleanExpression ltLastId(Long lastId, QParty party) {
		return lastId != null ? party.id.lt(lastId) : null;
	}
}
