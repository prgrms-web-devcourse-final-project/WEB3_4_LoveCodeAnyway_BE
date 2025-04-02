package com.ddobang.backend.domain.party.repository;

import java.time.LocalDate;
import java.util.List;

import com.ddobang.backend.domain.member.entity.QMember;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.entity.QParty;
import com.ddobang.backend.domain.party.entity.QPartyMember;
import com.ddobang.backend.domain.party.types.PartyMemberRole;
import com.ddobang.backend.domain.region.entity.QRegion;
import com.ddobang.backend.domain.store.entity.QStore;
import com.ddobang.backend.domain.theme.entity.QTheme;
import com.ddobang.backend.domain.theme.entity.QThemeTag;
import com.ddobang.backend.domain.theme.entity.QThemeTagMapping;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartyRepositoryImpl implements PartyRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Party> getParties(Long lastId, int size, PartySearchCondition partySearchCondition) {
		QParty party = QParty.party;
		QPartyMember partyMember = QPartyMember.partyMember;
		QMember host = QMember.member;
		QTheme theme = QTheme.theme;
		QStore store = QStore.store;
		QRegion region = QRegion.region;
		QThemeTagMapping mapping = QThemeTagMapping.themeTagMapping;
		QThemeTag tag = QThemeTag.themeTag;

		return queryFactory
			.selectFrom(party)
			.join(party.theme, theme).fetchJoin()
			.join(theme.store, store).fetchJoin()
			.join(store.region, region).fetchJoin()
			.join(party.partyMembers, partyMember)
			.on(partyMember.role.eq(PartyMemberRole.HOST))
			.join(partyMember.member, host).fetchJoin()
			.leftJoin(theme.themeTagMappings, mapping).fetchJoin()
			.leftJoin(mapping.themeTag, tag)
			.where(
				ltLastId(lastId),
				keywordMatch(partySearchCondition.keyword()),
				regionIn(partySearchCondition.regions()),
				dateIn(partySearchCondition.dates()),
				tagIn(partySearchCondition.tags())
			)
			.distinct()
			.orderBy(party.id.desc())
			.limit(size)
			.fetch();
	}

	private BooleanExpression ltLastId(Long lastId) {
		return lastId == null ? null : QParty.party.id.lt(lastId);
	}

	private BooleanExpression keywordMatch(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}

		QParty party = QParty.party;
		QTheme theme = QTheme.theme;
		QStore store = QStore.store;

		return party.title.containsIgnoreCase(keyword)
			.or(theme.name.containsIgnoreCase(keyword))
			.or(store.name.containsIgnoreCase(keyword));
	}

	private BooleanExpression regionIn(List<Long> regionIds) {
		if (regionIds == null || regionIds.isEmpty()) {
			return null;
		}
		return QStore.store.region.id.in(regionIds);
	}

	private BooleanExpression dateIn(List<LocalDate> dates) {
		if (dates == null || dates.isEmpty()) {
			return null;
		}

		return dates.stream()
			.map(date -> QParty.party.scheduledAt.between(
				date.atStartOfDay(),
				date.plusDays(1).atStartOfDay()
			))
			.reduce(BooleanExpression::or)
			.orElse(null);
	}

	private BooleanExpression tagIn(List<String> tags) {
		if (tags == null || tags.isEmpty()) {
			return null;
		}
		return QThemeTag.themeTag.name.in(tags);
	}
}
