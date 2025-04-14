package com.ddobang.backend.domain.party.testUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.entity.PartyMember;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeTag;

import jakarta.persistence.EntityManager;

public class TestDataHelper {

	// 생성 + DB 저장

	public static Region createRegion(EntityManager em, String major, String sub) {
		Region region = Region.builder()
			.majorRegion(major)
			.subRegion(sub)
			.build();
		em.persist(region);
		return region;
	}

	public static Store createStore(EntityManager em, Region region, String name) {
		Store store = Store.builder()
			.region(region)
			.name(name)
			.build();
		em.persist(store);
		return store;
	}

	public static ThemeTag createThemeTag(EntityManager em, String name) {
		ThemeTag tag = new ThemeTag(name);
		em.persist(tag);
		return tag;
	}

	public static Theme createTheme(EntityManager em, String name, String desc, Theme.Status status, Store store,
		List<ThemeTag> tags) {
		Theme theme = Theme.builder()
			.name(name)
			.description(desc)
			.minParticipants(4)
			.maxParticipants(5)
			.status(status)
			.store(store)
			.themeTags(tags)
			.build();
		em.persist(theme);
		return theme;
	}

	public static Member createMember(EntityManager em, String profileUrl, String nickname) {
		Member member = Member.builder()
			.nickname(nickname)
			.profilePictureUrl(profileUrl)
			.build();
		em.persist(member);
		return member;
	}

	public static Party createParty(EntityManager em, PartyRequest request, Theme theme) {
		Party party = Party.of(request, theme);
		em.persist(party);
		return party;
	}

	public static PartyMember createHost(EntityManager em, Party party, Member member) {
		PartyMember partyMember = PartyMember.createHost(party, member);
		em.persist(partyMember);
		return partyMember;
	}

	public static PartyMember createPartyMember(EntityManager em, Party party, Member member) {
		PartyMember partyMember = PartyMember.of(party, member);
		em.persist(partyMember);
		return partyMember;
	}

	// 객체만 반환

	public static Region createRegion(String major, String sub) {
		return Region.builder()
			.majorRegion(major)
			.subRegion(sub)
			.build();
	}

	public static Store createStore(Region region, String name) {
		return Store.builder()
			.region(region)
			.name(name)
			.build();
	}

	public static Theme createTheme(String name, String desc, Theme.Status status, Store store, List<ThemeTag> tags) {
		return Theme.builder()
			.name(name)
			.description(desc)
			.minParticipants(2)
			.maxParticipants(5)
			.status(status)
			.store(store)
			.themeTags(tags)
			.build();
	}

	public static Member createMember(String profileUrl, String nickname) {
		return Member.builder()
			.nickname(nickname)
			.profilePictureUrl(profileUrl)
			.build();
	}

	public static PartyMember createPartyMember(Party party, Member member) {
		return PartyMember.of(party, member);
	}

	public static PartyMember createHost(Party party, Member member) {
		return PartyMember.createHost(party, member);
	}

	// PartyRequest 생성

	public static PartyRequest partyReq(String title, Long themeId) {
		LocalDateTime defaultScheduledAt = LocalDate.now().plusDays(1).atTime(19, 0);
		return partyReq(title, themeId, defaultScheduledAt);
	}

	public static PartyRequest partyReq(String title, Long themeId, LocalDateTime scheduledAt) {
		return new PartyRequest(
			themeId,
			title,
			"테스트용 콘텐츠입니다.",
			scheduledAt,
			5,
			6,
			true
		);
	}
}
