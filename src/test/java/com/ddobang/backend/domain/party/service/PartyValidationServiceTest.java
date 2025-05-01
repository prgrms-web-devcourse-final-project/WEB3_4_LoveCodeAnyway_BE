package com.ddobang.backend.domain.party.service;

import static com.ddobang.backend.domain.party.testUtils.TestDataHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PartyValidationServiceTest {

	@Autowired
	EntityManager em;

	@Autowired
	PartyValidationService partyValidationService;

	Member host;
	Theme theme;

	@BeforeEach
	void setUp() {
		Region region = createRegion(em, "서울", "강남");
		Store store = createStore(em, region, "매장1");
		theme = createTheme(em, "테마1", "설명", Theme.Status.OPENED, store, List.of());
		host = createMember(em, "img.jpg", "호스트");
	}

	@DisplayName("HOST인 경우")
	@Test
	void checkHostTest1() {
		// given
		Party party = createParty(em, partyReq("파티", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		em.persist(party);

		// when & then
		assertDoesNotThrow(() -> partyValidationService.checkHost(party, host));
	}

	@Test
	@DisplayName("예외 - HOST가 아닌 경우")
	void checkHostTest2() {
		// given
		Party party = createParty(em, partyReq("파티", theme.getId()), theme);
		party.addPartyMember(createHost(party, host));
		Member guest = createMember(em, "img.jpg", "게스트");
		party.addPartyMember(createPartyMember(em, party, guest));

		em.persist(party);

		// when & then
		assertThrows(PartyException.class, () -> partyValidationService.checkHost(party, guest));
	}

	@Test
	@DisplayName("모집 중인 경우")
	void checkRecruitingTest1() {
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));
		party.updateStatus(PartyStatus.RECRUITING);

		em.persist(party);

		assertDoesNotThrow(() -> partyValidationService.checkRecruiting(party));
	}

	@Test
	@DisplayName("예외 - 모집 중이 아닌 경우")
	void checkRecruitingTest2() {
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(party, host));
		party.updateStatus(PartyStatus.PENDING);

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.checkRecruiting(party))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("에외 - 모임장이 신청한 경우")
	void validateApplyTest1() {
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.validateApply(party, host))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("예외 - 이미 승인된 모임원이 신청한 경우")
	void validateApplyTest2() {
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		Member guest = createMember(em, "img.jpg", "게스트");
		party.addPartyMember(createPartyMember(em, party, guest));

		party.updatePartyMemberStatus(guest, PartyMemberStatus.ACCEPTED);

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.validateApply(party, guest))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("조건에 맞는 멤버가 신청한 경우")
	void validateApplyTest3() {
		Member member = createMember(em, "img.jpg", "멤버");
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		em.persist(party);

		assertDoesNotThrow(() -> partyValidationService.validateApply(party, member));
	}

	@Test
	@DisplayName("예외 - 모임장이 취소한 경우")
	void validateCancelTest1() {
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.validateCancel(party, host))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("에외 - 모임원이 아닌데 취소한 경우")
	void validateCancelTest2() {
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		em.persist(party);

		Member guest = createMember(em, "img.jpg", "게스트");

		assertThatThrownBy(() -> partyValidationService.validateCancel(party, guest))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("신청한 모임원이 취소한 경우")
	void validateCancelTest3() {
		Member member = createMember(em, "img.jpg", "멤버");
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		party.addPartyMember(createPartyMember(em, party, member));

		em.persist(party);

		assertDoesNotThrow(() -> partyValidationService.validateApply(party, member));
	}

	@Test
	@DisplayName("승인된 모임원이 취소한 경우")
	void validateCancelTest4() {
		Member member = createMember(em, "img.jpg", "멤버");
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));
		party.addPartyMember(createPartyMember(em, party, member));
		party.updatePartyMemberStatus(member, PartyMemberStatus.ACCEPTED);

		em.persist(party);

		assertDoesNotThrow(() -> partyValidationService.validateCancel(party, member));
	}

	@Test
	@DisplayName("예외 - 모임원을 승인하는 경우")
	void validateAcceptTest1() {
		Member guest = createMember(em, "img.jpg", "게스트");
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.validateAccept(party, guest, host))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("에외 - 신청 취소한 멤버를 승인하는 경우")
	void validateAcceptTest2() {
		Member member = createMember(em, "img.jpg", "멤버");
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		party.addPartyMember(createPartyMember(em, party, member));
		party.updatePartyMemberStatus(member, PartyMemberStatus.CANCELLED);

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.validateAccept(party, member, host))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("예외 - 이미 승인된 멤버를 승인하는 경우")
	void validateAcceptTest3() {
		Member member = createMember(em, "img.jpg", "멤버");
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));

		party.addPartyMember(createPartyMember(em, party, member));
		party.updatePartyMemberStatus(member, PartyMemberStatus.ACCEPTED);

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.validateAccept(party, member, host))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("신청 상태의 멤버를 승인하는 경우")
	void validateAcceptTest4() {
		Member member = createMember(em, "img.jpg", "멤버");
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));
		party.addPartyMember(createPartyMember(em, party, member));

		em.persist(party);

		assertDoesNotThrow(() -> partyValidationService.validateAccept(party, member, host));
	}

	@Test
	@DisplayName("예외 - 모집 기간이고 상태가 PENDING이 아닌 경우")
	void validateExecutableTest1() {
		Party party = createParty(em, partyReq("모임", theme.getId(), LocalDateTime.now().plusDays(1)), theme);
		party.addPartyMember(createHost(em, party, host));
		party.updateStatus(PartyStatus.RECRUITING);

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.validateExecutable(party, host))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("상태가 PENDING인 경우")
	void validateExecutableTest2() {
		Party party = createParty(em, partyReq("모임", theme.getId(), LocalDateTime.now().plusDays(1)), theme);
		party.addPartyMember(createHost(em, party, host));
		party.updateStatus(PartyStatus.PENDING);

		em.persist(party);

		assertDoesNotThrow(() -> partyValidationService.validateExecutable(party, host));
	}

	@Test
	@DisplayName("예외 - 한 명이라도 신청한 모임을 수정하는 경우")
	void validateModifiableTest1() {
		Member member = createMember(em, "img.jpg", "멤버");
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));
		party.addPartyMember(createPartyMember(em, party, member));

		em.persist(party);

		assertThatThrownBy(() -> partyValidationService.validateModifiable(party, host))
			.isInstanceOf(PartyException.class);
	}

	@Test
	@DisplayName("아직 신청자가 없는 모임을 수정하는 경우")
	void validateModifiableTest2() {
		Party party = createParty(em, partyReq("모임", theme.getId()), theme);
		party.addPartyMember(createHost(em, party, host));
		em.persist(party);

		assertDoesNotThrow(() -> partyValidationService.validateModifiable(party, host));
	}
}
