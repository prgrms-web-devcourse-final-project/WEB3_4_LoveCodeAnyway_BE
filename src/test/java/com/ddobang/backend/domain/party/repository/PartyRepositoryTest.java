package com.ddobang.backend.domain.party.repository;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.testUtils.TestDataHelper;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.global.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ddobang.backend.domain.party.testUtils.TestDataHelper.partyReq;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@Transactional
public class PartyRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    PartyRepository partyRepository;

    @Test
    @DisplayName("전체 조회 - 필터 조건 없는 경우")
    void  findAllPartiesTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강남");
        Store store = TestDataHelper.createStore(em, region, "매장1");
        Theme theme = TestDataHelper.createTheme(em, "테마1", "설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");
        Member member = TestDataHelper.createMember(em, "img.jpg", "멤버");

        Party party1 = TestDataHelper.createParty(em, partyReq("테스트_모임1", theme.getId()), theme, host);
        Party party2 = TestDataHelper.createParty(em, partyReq("테스트_모임2", theme.getId()), theme, host);
        Party party3 = TestDataHelper.createParty(em, partyReq("테스트_모임3", theme.getId()), theme, host);

        TestDataHelper.createPartyMember(em, party1, member);
        TestDataHelper.createPartyMember(em, party2, member);
        TestDataHelper.createPartyMember(em, party3, member);

        em.flush();
        em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition(null, null, null, null);
        List<PartySummaryResponse> result = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(result).hasSize(3);

        List<String> titles = result.stream()
                .map(PartySummaryResponse::title)
                .toList();

        assertThat(titles).contains("테스트_모임1", "테스트_모임2", "테스트_모임3");
    }

    @Test
    @DisplayName("전체 조회 - 필터 조건 없는 경우 - RECRUITING/FULL 상태인 모임만")
    void  findAllRecruitingAndFullPartiesTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강남");
        Store store = TestDataHelper.createStore(em, region, "매장1");
        Theme theme = TestDataHelper.createTheme(em, "테마1", "설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        Party recruitingParty = TestDataHelper.createParty(em, partyReq("RECRUITING 파티", theme.getId()), theme, host);
        Party fullParty = TestDataHelper.createParty(em, partyReq("FULL 파티", theme.getId()), theme, host);
        Party pendingParty = TestDataHelper.createParty(em, partyReq("PENDING 파티", theme.getId()), theme, host);
        Party completedParty = TestDataHelper.createParty(em, partyReq("COMPLETED 파티", theme.getId()), theme, host);
        Party cancelledParty = TestDataHelper.createParty(em, partyReq("CANCELLED 파티", theme.getId()), theme, host);

        recruitingParty.updateStatus(PartyStatus.RECRUITING);
        fullParty.updateStatus(PartyStatus.FULL);
        pendingParty.updateStatus(PartyStatus.PENDING);
        completedParty.updateStatus(PartyStatus.COMPLETED);
        cancelledParty.updateStatus(PartyStatus.CANCELLED);

        em.flush();
        em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition(null, null, null, null);
        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        // then
        List<String> titles = results.stream()
                .map(PartySummaryResponse::title)
                .toList();

        assertThat(titles).contains("RECRUITING 파티", "FULL 파티");
        assertThat(titles).doesNotContain("PENDING 파티", "COMPLETED 파티", "CANCELLED 파티");
    }


    @Test
    @DisplayName("키워드 조회 - 테마 제목에 검색어가 있는 경우")
    void searchByThemeNameTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강남");
        Store store = TestDataHelper.createStore(em, region, "미스터리존");
        Theme theme1 = TestDataHelper.createTheme(em, "공포의 방", "무서운 설명", Theme.Status.OPENED, store, List.of());
        Theme theme2 = TestDataHelper.createTheme(em, "기쁨의 방", "밝은 설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        Party matchParty = TestDataHelper.createParty(em, partyReq("공포 테마 함께 해요", theme1.getId()), theme1, host);
        Party noMatchParty = TestDataHelper.createParty(em, partyReq("기쁨 테마", theme2.getId()), theme2, host);

        TestDataHelper.createPartyMember(em, matchParty, host);
        TestDataHelper.createPartyMember(em, noMatchParty, host);

        em.flush(); em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition("공포", null, null, null);
        List<PartySummaryResponse> result = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().themeName()).contains("공포");
    }

    @Test
    @DisplayName("키워드 조회 - 모임 제목에 검색어가 있는 경우")
    void searchByPartyNameTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강남");
        Store store = TestDataHelper.createStore(em, region, "미스터리존");
        Theme theme1 = TestDataHelper.createTheme(em, "공포의 방", "무서운 설명", Theme.Status.OPENED, store, List.of());
        Theme theme2 = TestDataHelper.createTheme(em, "기쁨의 방", "밝은 설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        Party matchParty = TestDataHelper.createParty(em, partyReq("공포 테마 함께 해요", theme1.getId()), theme1, host);
        Party noMatchParty = TestDataHelper.createParty(em, partyReq("기쁨 테마 같이~", theme2.getId()), theme2, host);

        TestDataHelper.createPartyMember(em, matchParty, host);
        TestDataHelper.createPartyMember(em, noMatchParty, host);

        em.flush(); em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition("같이", null, null, null);
        List<PartySummaryResponse> result = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().themeName()).contains("기쁨");
    }

    @Test
    @DisplayName("키워드 조회 - 매장 이름에 검색어가 있는 경우")
    void searchByStoreNameTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강남");
        Store store = TestDataHelper.createStore(em, region, "미스터리존");
        Theme theme1 = TestDataHelper.createTheme(em, "공포의 방", "무서운 설명", Theme.Status.OPENED, store, List.of());
        Theme theme2 = TestDataHelper.createTheme(em, "기쁨의 방", "밝은 설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        Party matchParty = TestDataHelper.createParty(em, partyReq("공포 테마 함께 해요", theme1.getId()), theme1, host);
        Party noMatchParty = TestDataHelper.createParty(em, partyReq("기쁨 테마 같이~", theme2.getId()), theme2, host);

        TestDataHelper.createPartyMember(em, matchParty, host);
        TestDataHelper.createPartyMember(em, noMatchParty, host);

        em.flush(); em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition("존", null, null, null);
        List<PartySummaryResponse> result = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("키워드 조회 - 호스트 닉네임에 키워드가 있는 경우")
    void searchByHostNameTest() {
        Region region = TestDataHelper.createRegion(em, "서울", "상수");
        Store store = TestDataHelper.createStore(em, region, "상수 매장");
        Theme theme = TestDataHelper.createTheme(em, "방탈출", "설명", Theme.Status.OPENED, store, List.of());

        Member host = TestDataHelper.createMember(em, "img.jpg", "공포의 호스트");
        TestDataHelper.createParty(em, TestDataHelper.partyReq("테스트 파티", theme.getId()), theme, host);

        em.flush();
        em.clear();

        PartySearchCondition condition = new PartySearchCondition("공포", null, null, null);
        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().hostNickname()).contains("공포");
    }

    @Test
    @DisplayName("키워드 조회 - 매장 이름, 테마 이름에 중복 검색어가 있는 경우")
    void findPartiesDuplicatedKeywordTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강남");
        Store store1 = TestDataHelper.createStore(em, region, "미스터리존");
        Store store2 = TestDataHelper.createStore(em, region, "공포존");
        Theme theme1 = TestDataHelper.createTheme(em, "공포의 방", "무서운 설명", Theme.Status.OPENED, store1, List.of());
        Theme theme2 = TestDataHelper.createTheme(em, "기쁨의 방", "밝은 설명", Theme.Status.OPENED, store2, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        Party matchParty = TestDataHelper.createParty(em, partyReq("공포 테마 함께 해요", theme1.getId()), theme1, host);
        Party noMatchParty = TestDataHelper.createParty(em, partyReq("기쁨 테마 같이~", theme2.getId()), theme2, host);

        TestDataHelper.createPartyMember(em, matchParty, host);
        TestDataHelper.createPartyMember(em, noMatchParty, host);

        em.flush(); em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition("공포", null, null, null);
        List<PartySummaryResponse> result = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("지역 필터 - 특정 지역 테마 조회")
    void regionFilterTest() {
        Region region1 = TestDataHelper.createRegion(em, "서울", "강남");
        Region region2 = TestDataHelper.createRegion(em, "서울", "홍대");

        Store store1 = TestDataHelper.createStore(em, region1, "강남매장");
        Store store2 = TestDataHelper.createStore(em, region2, "홍대매장");

        Theme theme1 = TestDataHelper.createTheme(em, "테마1", "설명", Theme.Status.OPENED, store1, List.of());
        Theme theme2 = TestDataHelper.createTheme(em, "테마2", "설명", Theme.Status.OPENED, store2, List.of());

        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        TestDataHelper.createParty(em, TestDataHelper.partyReq("강남파티", theme1.getId()), theme1, host);
        TestDataHelper.createParty(em, TestDataHelper.partyReq("홍대파티", theme2.getId()), theme2, host);

        em.flush();
        em.clear();

        PartySearchCondition condition = new PartySearchCondition(null, List.of(region1.getId()), null, null);
        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("강남파티");
    }

    @Test
    @DisplayName("날짜 필터 - 특정 날짜의 모임 조회")
    void dateFilterTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "잠실");
        Store store = TestDataHelper.createStore(em, region, "잠실 매장");
        Theme theme = TestDataHelper.createTheme(em, "잠실 테마", "설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        LocalDate targetDate = LocalDate.of(2025, 4, 10);
        LocalDateTime scheduledAt = LocalDate.of(2025, 4, 10).atTime(17, 0);

        TestDataHelper.createParty(em, TestDataHelper.partyReq("4월 10일 파티", theme.getId(), scheduledAt), theme, host);

        em.flush();
        em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition(null, null, List.of(targetDate), null);
        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("4월 10일 파티");
    }

    @Test
    @DisplayName("날짜 필터 - 시간이 상관 없이 날짜로 조회")
    void dateFilterCoversFullDayTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강동");
        Store store = TestDataHelper.createStore(em, region, "매장");
        Theme theme = TestDataHelper.createTheme(em, "테마", "설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        // 같은 날짜, 다른 시간
        LocalDate targetDate = LocalDate.of(2025, 4, 10);

        TestDataHelper.createParty(em, TestDataHelper.partyReq("오전 파티", theme.getId(), targetDate.atTime(9, 0)), theme, host);
        TestDataHelper.createParty(em, TestDataHelper.partyReq("오후 파티", theme.getId(), targetDate.atTime(20, 0)), theme, host);

        em.flush();
        em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition(null, null, List.of(targetDate), null);
        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(results).hasSize(2);
        List<String> titles = results.stream().map(PartySummaryResponse::title).toList();
        assertThat(titles).contains("오전 파티", "오후 파티");
    }

    @Test
    @DisplayName("태그 필터 - 특정 테마 태그가 포함된 경우만 조회")
    void tagFilterTest() {
        Region region = TestDataHelper.createRegion(em, "서울", "이태원");
        Store store = TestDataHelper.createStore(em, region, "매장");
        ThemeTag horror = TestDataHelper.createThemeTag(em, "공포");
        Theme theme = TestDataHelper.createTheme(em, "귀신 테마", "설명", Theme.Status.OPENED, store, List.of(horror));

        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        TestDataHelper.createParty(em, TestDataHelper.partyReq("좀비 파티", theme.getId()), theme, host);

        em.flush();
        em.clear();

        PartySearchCondition condition = new PartySearchCondition(null, null, null, List.of("공포"));
        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("좀비 파티");
    }

    @Test
    @DisplayName("태그 필터 - 여러 태그 중 하나만 일치해도 조회")
    void tagFilterWithMultipleTagsTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "이태원");
        Store store = TestDataHelper.createStore(em, region, "이태원 매장");

        ThemeTag horror = TestDataHelper.createThemeTag(em, "공포");
        ThemeTag thriller = TestDataHelper.createThemeTag(em, "스릴러");
        ThemeTag mystery = TestDataHelper.createThemeTag(em, "미스터리");

        // 테마에 태그 여러 개 등록
        Theme theme = TestDataHelper.createTheme(em, "복합 테마", "설명", Theme.Status.OPENED, store, List.of(horror, thriller, mystery));

        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        TestDataHelper.createParty(em, TestDataHelper.partyReq("복합 태그 파티", theme.getId()), theme, host);

        em.flush();
        em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition(null, null, null, List.of("스릴러"));
        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("복합 태그 파티");
    }

    @Test
    @DisplayName("복합 필터 - 지역 + 태그 + 날짜를 모두 만족하는 파티만 조회")
    void regionTagDateCombinedFilterTest() {
        // given
        Region regionA = TestDataHelper.createRegion(em, "서울", "강남");
        Region regionB = TestDataHelper.createRegion(em, "서울", "홍대");

        Store storeA = TestDataHelper.createStore(em, regionA, "공포 매장");
        Store storeB = TestDataHelper.createStore(em, regionB, "로맨스 매장");

        ThemeTag horror = TestDataHelper.createThemeTag(em, "공포");
        ThemeTag romance = TestDataHelper.createThemeTag(em, "로맨스");

        Theme horrorTheme = TestDataHelper.createTheme(em, "공포 테마", "무서운 테마", Theme.Status.OPENED, storeA, List.of(horror));
        Theme romanceTheme = TestDataHelper.createTheme(em, "로맨스 테마", "달달한 테마", Theme.Status.OPENED, storeB, List.of(romance));

        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        LocalDate targetDate = LocalDate.of(2025, 4, 10);

        TestDataHelper.createParty(em, partyReq("조건 만족 파티", horrorTheme.getId(), targetDate.atTime(18, 0)), horrorTheme, host);
        TestDataHelper.createParty(em, partyReq("지역 다른 파티", romanceTheme.getId(), targetDate.atTime(18, 0)), romanceTheme, host);
        TestDataHelper.createParty(em, partyReq("태그 다른 파티", romanceTheme.getId(), targetDate.atTime(18, 0)), romanceTheme, host);
        TestDataHelper.createParty(em, partyReq("날짜 다른 파티", horrorTheme.getId(), targetDate.minusDays(1).atTime(18, 0)), horrorTheme, host);

        em.flush();
        em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition(
                null,
                List.of(regionA.getId()),
                List.of(targetDate),
                List.of("공포")
        );

        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("조건 만족 파티");
    }

    @Test
    @DisplayName("복합 필터 - 검색어 + 지역 + 태그 + 날짜를 모두 만족하는 파티만 조회")
    void keywordRegionTagDateCombinedFilterTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강남");
        Store store = TestDataHelper.createStore(em, region, "공포존");

        ThemeTag horror = TestDataHelper.createThemeTag(em, "공포");
        ThemeTag romance = TestDataHelper.createThemeTag(em, "로맨스");

        Theme horrorTheme = TestDataHelper.createTheme(em, "공포의 방", "무서운 설명", Theme.Status.OPENED, store, List.of(horror));
        Theme romanceTheme = TestDataHelper.createTheme(em, "사랑의 방", "설렘 설명", Theme.Status.OPENED, store, List.of(romance));

        Member host = TestDataHelper.createMember(em, "img.jpg", "공포호스트");

        LocalDate targetDate = LocalDate.of(2025, 4, 10);
        LocalDateTime scheduledAt = targetDate.atTime(19, 0);

        TestDataHelper.createParty(em, partyReq("미스터리 공포 파티", horrorTheme.getId(), scheduledAt), horrorTheme, host);

        TestDataHelper.createParty(em, partyReq("일반 공포 파티", horrorTheme.getId(), scheduledAt), horrorTheme, host);
        TestDataHelper.createParty(em, partyReq("미스터리 로맨스 파티", romanceTheme.getId(), scheduledAt), romanceTheme, host);
        TestDataHelper.createParty(em, partyReq("미스터리 공포 파티 - 전날", horrorTheme.getId(), targetDate.minusDays(1).atTime(18, 0)), horrorTheme, host);

        em.flush();
        em.clear();

        // when
        PartySearchCondition condition = new PartySearchCondition(
                "미스터리",
                List.of(region.getId()),
                List.of(targetDate),
                List.of("공포")
        );

        List<PartySummaryResponse> results = partyRepository.getParties(null, 10, condition);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("미스터리 공포 파티");
    }

    @Test
    @DisplayName("페이징 - lastId와 size 조건에 따라 조회")
    void pagingTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "합정");
        Store store = TestDataHelper.createStore(em, region, "합정 매장");
        Theme theme = TestDataHelper.createTheme(em, "합정 테마", "설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        List<Party> parties = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            parties.add(TestDataHelper.createParty(em, partyReq("파티" + i, theme.getId()), theme, host));
        }

        em.flush();
        em.clear();

        // when
        Long lastId = parties.get(4).getId();
        PartySearchCondition condition = new PartySearchCondition(null, null, null, null);

        List<PartySummaryResponse> results = partyRepository.getParties(lastId, 2, condition);

        // then
        assertThat(results).hasSize(2);
        List<String> titles = results.stream().map(PartySummaryResponse::title).toList();
        assertThat(titles).containsExactly("파티4", "파티3");
    }

    @Test
    @DisplayName("페이징 + 필터 - 필터를 만족하는 파티 중 lastId 이전 최신 파티만 1개 조회")
    void pagingWithFiltersTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "강남");
        Store store = TestDataHelper.createStore(em, region, "강남 매장");

        ThemeTag horror = TestDataHelper.createThemeTag(em, "공포");
        Theme theme = TestDataHelper.createTheme(em, "공포 테마", "설명", Theme.Status.OPENED, store, List.of(horror));

        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        LocalDate targetDate = LocalDate.of(2025, 4, 10);

        List<Party> parties = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            parties.add(TestDataHelper.createParty(em,
                    partyReq("파티" + i, theme.getId(), targetDate.atTime(18 + i, 0)), theme, host));
        }

        em.flush();
        em.clear();

        // when
        Long lastId = parties.get(2).getId();
        PartySearchCondition condition = new PartySearchCondition(
                null,
                List.of(region.getId()),
                List.of(targetDate),
                List.of("공포")
        );

        List<PartySummaryResponse> results = partyRepository.getParties(lastId, 1, condition);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("파티2");
    }

    @Test
    @DisplayName("검색어 + 페이징 - 키워드에 맞는 파티 중에서 lastId 이전 최신 1개 조회")
    void keywordPagingTest() {
        // given
        Region region = TestDataHelper.createRegion(em, "서울", "서초");
        Store store = TestDataHelper.createStore(em, region, "미스터리존");
        Theme theme = TestDataHelper.createTheme(em, "미스터리룸", "설명", Theme.Status.OPENED, store, List.of());
        Member host = TestDataHelper.createMember(em, "img.jpg", "호스트");

        List<Party> parties = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            parties.add(TestDataHelper.createParty(em, partyReq("미스터리 파티" + i, theme.getId()), theme, host));
        }

        em.flush();
        em.clear();

        // when
        Long lastId = parties.get(2).getId();
        PartySearchCondition condition = new PartySearchCondition("미스터리", null, null, null);

        List<PartySummaryResponse> results = partyRepository.getParties(lastId, 1, condition);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("미스터리 파티2");
    }
}
