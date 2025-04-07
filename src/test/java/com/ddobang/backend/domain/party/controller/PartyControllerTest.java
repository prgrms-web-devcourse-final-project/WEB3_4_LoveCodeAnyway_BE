// package com.ddobang.backend.domain.party.controller;
//
// import com.ddobang.backend.domain.member.entity.Member;
// import com.ddobang.backend.domain.member.repository.MemberRepository;
// import com.ddobang.backend.domain.party.PartyAuthHelper;
// import com.ddobang.backend.domain.party.dto.request.PartyRequest;
// import com.ddobang.backend.domain.party.entity.Party;
// import com.ddobang.backend.domain.party.repository.PartyRepository;
// import com.ddobang.backend.domain.party.testUtils.MockConfig;
// import com.ddobang.backend.domain.party.testUtils.TestDataHelper;
// import com.ddobang.backend.domain.party.types.PartyStatus;
// import com.ddobang.backend.domain.region.entity.Region;
// import com.ddobang.backend.domain.region.repository.RegionRepository;
// import com.ddobang.backend.domain.store.entity.Store;
// import com.ddobang.backend.domain.store.repository.StoreRepository;
// import com.ddobang.backend.domain.theme.entity.Theme;
// import com.ddobang.backend.domain.theme.repository.ThemeRepository;
// import com.ddobang.backend.global.security.TestSecurityConfig;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;
//
// import java.util.List;
//
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// @SpringBootTest
// @ActiveProfiles("test")
// @AutoConfigureMockMvc
// @Import({TestSecurityConfig.class, MockConfig.class})
// @Transactional
// public class PartyControllerTest {
//
//     @Autowired
//     private MockMvc mockMvc;
//
//     @Autowired
//     private PartyRepository partyRepository;
//
//     @Autowired
//     private ThemeRepository themeRepository;
//
//     @Autowired
//     private StoreRepository storeRepository;
//
//     @Autowired
//     private RegionRepository regionRepository;
//
//     @Autowired
//     private MemberRepository memberRepository;
//
//     @Autowired
//     private PartyAuthHelper partyAuthHelper;
//
//     private Theme theme;
//     private Member host;
//     private Party party;
//
//     @BeforeEach
//     void setUp() {
//         Region region = regionRepository.save(TestDataHelper.createRegion("서울", "강남"));
//         Store store = storeRepository.save(TestDataHelper.createStore(region, "테스트매장"));
//         theme = themeRepository.save(TestDataHelper.createTheme("공포", "무서운 테마", Theme.Status.OPENED, store, List.of()));
//         host = memberRepository.save(TestDataHelper.createMember("host.jpg", "호스트"));
//
//         PartyRequest request = TestDataHelper.partyReq("테스트모임", theme.getId());
//         party = partyRepository.save(Party.of(request, theme, host));
//     }
//
//     @Test
//     @DisplayName("모임 목록 조회")
//     void getPartiesTest() throws Exception {
//         // when
//         mockMvc.perform(get("/api/v1/parties"))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("getParties"))
//                 .andExpect(status().isOk());
//     }
//
//     @Test
//     @DisplayName("모임 상세 조회")
//     void getPartyTest() throws Exception {
//         mockMvc.perform(get("/api/v1/parties/{id}", party.getId()))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("getParty"))
//                 .andExpect(status().isOk());
//     }
//
//     @Test
//     @DisplayName("모임 등록")
//     void createPartyTest() throws Exception {
//         PartyRequest request = TestDataHelper.partyReq("모임등록", theme.getId());
//
//         mockMvc.perform(post("/api/v1/parties")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(new ObjectMapper().writeValueAsString(request)))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("createParty"))
//                 .andExpect(status().isCreated());
//     }
//
//     @Test
//     @DisplayName("모임 수정")
//     void modifyPartyTest() throws Exception {
//         when(partyAuthHelper.getCurrentMember()).thenReturn(host);
//
//         PartyRequest request = TestDataHelper.partyReq("모임 수정", theme.getId());
//
//         mockMvc.perform(put("/api/v1/parties/{id}", party.getId())
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(new ObjectMapper().writeValueAsString(request)))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("modifyParty"))
//                 .andExpect(status().isOk());
//     }
//
//     @Test
//     @DisplayName("모임 삭제")
//     void softDeletePartyTest() throws Exception {
//         when(partyAuthHelper.getCurrentMember()).thenReturn(host);
//
//         mockMvc.perform(delete("/api/v1/parties/{id}", party.getId()))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("softDeleteParty"))
//                 .andExpect(status().isNoContent());
//     }
//
//     @Test
//     @DisplayName("모임 참가 신청")
//     void applyPartyTest() throws Exception {
//         Member applicant = memberRepository.save(TestDataHelper.createMember("imgUrl", "신청자"));
//
//         when(partyAuthHelper.getCurrentMember()).thenReturn(applicant);
//
//         mockMvc.perform(post("/api/v1/parties/{id}/apply", party.getId()))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("applyParty"))
//                 .andExpect(status().isNoContent());
//     }
//
//     @Test
//     @DisplayName("모임 참가 신청 쉬소")
//     void cancelAppliedPartyTest() throws Exception {
//         Member applicant = memberRepository.save(TestDataHelper.createMember("imgUrl", "신청자"));
//
//         when(partyAuthHelper.getCurrentMember()).thenReturn(applicant);
//
//         mockMvc.perform(post("/api/v1/parties/{id}/apply", party.getId()))
//                 .andExpect(status().isNoContent());
//
//         mockMvc.perform(delete("/api/v1/parties/{id}/cancel", party.getId()))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("cancelAppliedParty"))
//                 .andExpect(status().isNoContent());
//     }
//
//     @Test
//     @DisplayName("모임 신청 승인")
//     void acceptPartyMemberTest() throws Exception {
//         Member member = memberRepository.save(TestDataHelper.createMember("imgUrl", "모임원"));
//
//         when(partyAuthHelper.getCurrentMember()).thenReturn(member);
//
//         mockMvc.perform(post("/api/v1/parties/{id}/apply", party.getId()))
//                 .andExpect(status().isNoContent());
//
//         when(partyAuthHelper.getCurrentMember()).thenReturn(host);
//
//         mockMvc.perform(post("/api/v1/parties/{id}/accept/{memberId}", party.getId(), member.getId()))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("acceptPartyMember"))
//                 .andExpect(status().isNoContent());
//     }
//
//     @Test
//     @DisplayName("모임 실행 완료")
//     void executePartyTest() throws Exception {
//         when(partyAuthHelper.getCurrentMember()).thenReturn(host);
//
//         party.updateStatus(PartyStatus.PENDING);
//         partyRepository.save(party);
//
//         mockMvc.perform(patch("/api/v1/parties/{id}/executed", party.getId()))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("executeParty"))
//                 .andExpect(status().isNoContent());
//     }
//
//     @Test
//     @DisplayName("모임 미실행 완료")
//     void unexecutePartyTest() throws Exception {
//         when(partyAuthHelper.getCurrentMember()).thenReturn(host);
//
//         party.updateStatus(PartyStatus.PENDING);
//         partyRepository.save(party);
//
//         mockMvc.perform(patch("/api/v1/parties/{id}/unexecuted", party.getId()))
//                 .andExpect(handler().handlerType(PartyController.class))
//                 .andExpect(handler().methodName("unexecuteParty"))
//                 .andExpect(status().isNoContent());
//     }
// }
