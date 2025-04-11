package com.ddobang.backend.domain.party.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.party.dto.PartyDto;
import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartyMainResponse;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.repository.PartyRepository;
import com.ddobang.backend.domain.party.testUtils.TestDataHelper;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.exception.ThemeErrorCode;
import com.ddobang.backend.domain.theme.exception.ThemeException;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.event.EventPublisher;
import com.ddobang.backend.global.response.PageDto;
import com.ddobang.backend.global.response.SliceDto;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

	@InjectMocks
	private PartyService partyService;

	@Mock
	private ThemeService themeService;

	@Mock
	private PartyRepository partyRepository;

	@Mock
	private MemberService memberService;

	@Mock
	private PartyValidationService partyValidationService;

	@Mock
	private EventPublisher eventPublisher; // 이벤트 퍼블리셔 추가

	private Theme theme;
	private Member host;
	private Party party;

	@SuppressWarnings("checkstyle:RegexpSinglelineJava")
	@BeforeEach
	void setUp() {
		Region region = TestDataHelper.createRegion("서울", "강남");
		Store store = TestDataHelper.createStore(region, "매장1");
		theme = TestDataHelper.createTheme("테마1", "설명", Theme.Status.OPENED, store, List.of());
		host = TestDataHelper.createMember("img.jpg", "멤버");
		PartyRequest partyReq = TestDataHelper.partyReq("모임", theme.getId());
		party = Party.of(partyReq, theme, host);
	}

	@Test
	@DisplayName("파티 목록 조회")
	void getPartiesTest() {
		// given
		int size = 1;
		PartySearchCondition searchCondition = new PartySearchCondition(null, null, null, null);
		List<PartySummaryResponse> expectedParties =
			List.of(PartySummaryResponse.from(party));

		when(partyRepository.getParties(null, size + 1, searchCondition)).thenReturn(expectedParties);

		// when
		SliceDto<PartySummaryResponse> result = partyService.getParties(null, size, searchCondition);

		// then
		assertNotNull(result);
		assertEquals(expectedParties.size(), result.content().size());
		assertFalse(result.hasNext());

		verify(partyRepository).getParties(null, size + 1, searchCondition);
	}

	@Test
	@DisplayName("파티 조회")
	void getPartyByIdTest1() {
		// given
		Long partyId = 1L;
		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));

		// when
		Party result = partyService.getPartyById(partyId);

		// then
		assertNotNull(result);
		assertEquals(party, result);
		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("파티 상세 조회 - 파티가 없을 경우")
	void getPartyByIdTest2() {
		// given
		Long partyId = 1L;
		when(partyRepository.findById(partyId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(PartyException.class, () -> partyService.getPartyById(partyId));

		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("모임 생성")
	void createPartyTest1() {
		// given
		PartyRequest request = new PartyRequest(
			theme.getId(), "모임", "모임 섦명", LocalDateTime.now().plusDays(1), 2, 5, true);

		when(themeService.getThemeById(theme.getId())).thenReturn(theme);
		when(partyRepository.save(any(Party.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		PartyDto result = partyService.createParty(request, host);

		// then
		assertNotNull(result);
		assertEquals(request.title(), result.title());
		assertEquals(theme.getId(), result.themeId());
		assertEquals(host.getId(), result.hostId());

		verify(themeService).getThemeById(theme.getId());
	}

	@Test
	@DisplayName("모임 생성 - 테마가 없을 경우")
	void createPartyTest2() {
		// given
		Long invalidThemeId = 999L;
		PartyRequest request = new PartyRequest(
			invalidThemeId,
			"모임1",
			"모임 설명",
			LocalDateTime.now().plusDays(1),
			2,
			5,
			true
		);

		when(themeService.getThemeById(invalidThemeId))
			.thenThrow(new ThemeException(ThemeErrorCode.THEME_NOT_FOUND));

		// when & then
		assertThrows(ThemeException.class, () -> partyService.createParty(request, host));

		verify(themeService).getThemeById(invalidThemeId);
	}

	@Test
	@DisplayName("모임 수정")
	void modifyPartyTest1() {
		// given
		Long partyId = 1L;
		PartyRequest request = new PartyRequest(
			theme.getId(),
			"수정된 모임",
			"설명 수정",
			LocalDateTime.now().plusDays(3),
			3,
			6,
			false
		);

		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
		when(themeService.getThemeById(request.themeId())).thenReturn(theme);

		// when
		PartyDto result = partyService.modifyParty(partyId, request, host);

		// then
		assertNotNull(result);
		assertEquals(request.title(), result.title());
		assertEquals(request.content(), result.content());
		assertEquals(request.themeId(), result.themeId());

		verify(partyValidationService).validateModifiable(party, host);
		verify(themeService).getThemeById(request.themeId());
		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("파티 삭제")
	void softDeletePartyTest() {
		// given
		Long partyId = 1L;

		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));

		// when
		partyService.softDeleteParty(partyId, host);

		// then
		assertTrue(party.getDeleted());
		assertEquals("모임", party.getTitle());

		// verify
		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("모임 참가")
	void applyPartyTest1() {
		// given
		Long partyId = 1L;
		Member actor = TestDataHelper.createMember("imgUrl", "신청자");
		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));

		// when
		partyService.applyParty(partyId, actor);

		// then
		assertTrue(party.isPartyMember(actor));
		assertEquals(PartyMemberStatus.APPLICANT, party.getPartyMemberStatus(actor));

		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("모임 참가")
	void applyPartyTest2() {
		// given
		Long partyId = 1L;
		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));

		Member actor = TestDataHelper.createMember("imgUrl", "신청자");
		party.addPartyMember(actor);
		party.updatePartyMemberStatus(actor, PartyMemberStatus.CANCELLED);

		// when
		partyService.applyParty(partyId, actor);

		// then
		assertTrue(party.isPartyMember(actor));
		assertEquals(PartyMemberStatus.APPLICANT, party.getPartyMemberStatus(actor));

		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("모임 신청 취소")
	void cancelAppliedPartyTest() {
		// given
		Long partyId = 1L;
		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));

		Member actor = TestDataHelper.createMember("imgUrl", "취소자");
		party.addPartyMember(actor);

		// when
		partyService.cancelAppliedParty(partyId, actor);

		// then
		assertTrue(party.isPartyMember(actor));
		assertEquals(PartyMemberStatus.CANCELLED, party.getPartyMemberStatus(actor));

		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("파티 멤버 수락")
	void acceptPartyMemberTest() {
		// given
		Long partyId = 1L;
		Long memberId = 2L;
		Member applicant = TestDataHelper.createMember("imgUrl", "신청자");
		party.addPartyMember(applicant);
		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
		when(memberService.getMember(memberId)).thenReturn(applicant);

		// when
		partyService.acceptPartyMember(partyId, memberId, host);

		// then
		assertTrue(party.isPartyMember(applicant));
		assertEquals(PartyMemberStatus.ACCEPTED, party.getPartyMemberStatus(applicant));

		verify(partyRepository).findById(partyId);
		verify(memberService).getMember(memberId);
	}

	@Test
	@DisplayName("파티 실행 완료")
	void executePartyTest() {
		// given
		Long partyId = 1L;
		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));

		// when
		partyService.executeParty(partyId, host);

		// then
		assertEquals(PartyStatus.COMPLETED, party.getStatus());

		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("파티 미실행 완료")
	void unexecutePartyTest() {
		// given
		Long partyId = 1L;
		when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));

		// when
		partyService.unexecuteParty(partyId, host);

		// then
		assertEquals(PartyStatus.CANCELLED, party.getStatus());

		verify(partyRepository).findById(partyId);
	}

	@Test
	@DisplayName("메인 페이지")
	void getPartiesForMainTest() {
		// given
		List<Party> mockParties = IntStream.range(0, 12)
			.mapToObj(i -> {
				LocalDateTime scheduledAt = LocalDateTime.now().plusDays(i);
				PartyRequest request = new PartyRequest(
					theme.getId(),
					"Party " + i,
					"내용 " + i,
					scheduledAt,
					5,
					6,
					true
				);
				return Party.of(request, theme, host);
			})
			.collect(Collectors.toList());

		when(partyRepository.findTop12ByStatusOrderByScheduledAtAsc(PartyStatus.RECRUITING))
			.thenReturn(mockParties);

		// when
		List<PartyMainResponse> result = partyService.getUpcomingParties();

		// then
		assertThat(result).hasSize(12);
		assertThat(result.getFirst().title()).isEqualTo("Party 0");
		assertThat(result.get(11).title()).isEqualTo("Party 11");

		for (int i = 1; i < result.size(); i++) {
			assertThat(result.get(i).scheduledAt())
				.isAfterOrEqualTo(result.get(i - 1).scheduledAt());
		}

		verify(partyRepository, times(1))
			.findTop12ByStatusOrderByScheduledAtAsc(PartyStatus.RECRUITING);
	}

	@Test
	@DisplayName("참여한 파티 목록 조회")
	void getJoinedPartiesTest() {
		// given
		int page = 0;
		int size = 1;
		Pageable pageable = PageRequest.of(page, size);
		PartySummaryResponse expectedResponse = PartySummaryResponse.from(party);
		Page<PartySummaryResponse> expectedPage = new PageImpl<>(List.of(expectedResponse), pageable, 1);

		when(partyRepository.findByMemberJoined(host, pageable, true)).thenReturn(expectedPage);

		// when
		PageDto<PartySummaryResponse> result = partyService.getMyJoinedParties(host, page, size);

		// then
		assertNotNull(result);
		assertEquals(expectedPage.getContent().size(), result.items().size());
		assertEquals(1, result.items().size());
		assertEquals(expectedResponse, result.items().getFirst());
		verify(partyRepository).findByMemberJoined(host, pageable, true);
	}
}
