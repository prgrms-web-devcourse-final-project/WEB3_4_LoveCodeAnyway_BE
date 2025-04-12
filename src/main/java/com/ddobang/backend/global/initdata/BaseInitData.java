package com.ddobang.backend.global.initdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.service.DiaryService;
import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.entity.PartyMember;
import com.ddobang.backend.domain.party.repository.PartyMemberRepository;
import com.ddobang.backend.domain.party.repository.PartyRepository;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.repository.RegionRepository;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.repository.StoreRepository;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.domain.theme.repository.ThemeRepository;
import com.ddobang.backend.domain.theme.repository.ThemeTagRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
	private final RegionRepository regionRepository;
	private final StoreRepository storeRepository;
	private final ThemeRepository themeRepository;
	private final ThemeTagRepository themeTagRepository;
	private final MemberRepository memberRepository;
	private final DiaryService diaryService;
	private final PartyRepository partyRepository;
	private final PartyMemberRepository partyMemberRepository;

	@Autowired
	@Lazy
	private BaseInitData self;

	@Getter
	private Region region1;
	@Getter
	private Region region2;

	@Getter
	private Store store1;
	@Getter
	private Store store2;
	@Getter
	private ThemeTag tag1;
	@Getter
	private ThemeTag tag2;
	@Getter
	private ThemeTag tag3;

	@Getter
	private List<Theme> themes = new ArrayList<>();

	@Bean
	public ApplicationRunner baseInitDataApplicationRunner() {
		return args -> {
			self.memberInitData();
			self.themeInitData();
			self.diaryInitData();
			self.partyInitData();
		};
	}

	// Member init data
	@Transactional
	public void memberInitData() {
		if (memberRepository.count() > 0) {
			return;
		}

		// 테스트용 회원 생성
		Member member1 = Member.builder()
			.nickname("testUser1")
			.build();

		Member member2 = Member.builder()
			.nickname("testUser2")
			.build();

		memberRepository.save(member1);
		memberRepository.save(member2);
	}

	// Theme init data
	@Transactional
	public void themeInitData() {
		if (themeRepository.count() > 0) {
			return;
		}

		if (themeRepository.count() > 0)
			return;

		// 1. 지역 2개 저장
		region1 = regionRepository.save(new Region("서울", "강남"));
		region2 = regionRepository.save(new Region("서울", "홍대"));

		// 2. 매장 2개 저장
		store1 = storeRepository.save(Store.builder()
			.name("방탈출 A")
			.address("서울 강남구")
			.phoneNumber("010-1111-1111")
			.status(Store.Status.OPENED)
			.region(region1)
			.build());

		store2 = storeRepository.save(Store.builder()
			.name("방탈출 B")
			.address("서울 마포구")
			.phoneNumber("010-2222-2222")
			.status(Store.Status.OPENED)
			.region(region2)
			.build());

		// 3. 태그 2개 저장
		tag1 = themeTagRepository.save(new ThemeTag("공포"));
		tag2 = themeTagRepository.save(new ThemeTag("감성"));
		tag3 = themeTagRepository.save(new ThemeTag("판타지"));

		// 4. 테마 10개 저장
		themes = IntStream.range(1, 11)
			.mapToObj(i -> themeRepository.save(Theme.builder()
				.name("테마 " + i)
				.description("테마 설명 " + i)
				.officialDifficulty(3.0f)
				.runtime(60)
				.minParticipants(i % 2 == 0 ? 2 : 4)
				.maxParticipants(i % 2 == 0 ? 3 : 5)
				.price(25000)
				.status(i % 3 != 0 ? Theme.Status.OPENED : Theme.Status.CLOSED)
				.reservationUrl("https://example.com/theme/" + i)
				.thumbnailUrl("https://www.roomlescape.com/file/theme_info/1723787821_10bd760472.gif")
				.store(i % 2 == 0 ? store1 : store2)
				.themeTags(i % 4 != 0 ? List.of(tag1, tag2) : List.of(tag3))
				.build()))
			.toList();
	}

	// Diary init data
	@Transactional
	public void diaryInitData() {
		if (diaryService.getItemsAll(1, 10).getTotalElements() > 0) {
			return;
		}

		Member member = memberRepository.findByNickname("testUser1").orElseThrow();

		for (int i = 1; i <= 9; i++) {
			diaryService.save(
				member,
				DiaryRequestDto.builder()
					.themeId((long)i)
					.escapeDate(LocalDate.of(2024, i, 15))
					.participants("지인1, 지인2")
					.difficulty(3)
					.fear(3)
					.activity(3)
					.satisfaction(3)
					.production(3)
					.story(3)
					.question(3)
					.interior(3)
					.deviceRatio(70)
					.hintCount(i % 3)
					.escapeResult(i % 2 == 0 ? true : false)
					.timeType("REMAINING")
					.elapsedTime("15:25")
					.review("너무 재밌었다!!")
					.build()
			);
		}
	}

	@Transactional
	public void partyInitData() {
		if (partyRepository.count() > 0) {
			return;
		}

		// user0 ~ user4
		List<Member> members = IntStream.range(0, 5)
			.mapToObj(i -> {
				String nickname = "user" + i;
				return memberRepository.existsByNickname(nickname)
					? memberRepository.findByNickname(nickname).orElseThrow()
					: memberRepository.save(Member.of(nickname,
					Math.random() < 0.5 ? Gender.MALE : Gender.FEMALE,
					"소개",
					"image.url"));
			})
			.toList();

		List<Theme> themeList = themeRepository.findAll();

		List<Party> parties = themeList.stream()
			.flatMap(theme -> IntStream.range(0, 3)
				.mapToObj(i -> {
					// 1. 랜덤 호스트 선택
					Member host = members.get((int)(Math.random() * members.size()));

					// 2. 파티 생성
					PartyRequest request = new PartyRequest(
						theme.getId(),
						theme.getName() + "모임_" + i,
						"모임 소개",
						LocalDateTime.now().plusDays((int)(Math.random() * 6 + 5)),
						theme.getMaxParticipants() - 2,
						theme.getMaxParticipants(),
						Math.random() < 0.5
					);
					Party party = partyRepository.save(Party.of(request, theme));
					party.addPartyMember(partyMemberRepository.save(PartyMember.createHost(party, host)));

					// 3. 신청자 = host 제외한 나머지
					List<Member> otherMembers = members.stream()
						.filter(m -> !m.equals(host))
						.collect(Collectors.toList());

					Collections.shuffle(otherMembers);
					int applicantCount = (int)(Math.random() * 3) + 1;

					otherMembers.stream()
						.limit(applicantCount)
						.forEach(applicant -> {
							if (party.isPartyMember(applicant))
								return;

							PartyMember partyMember = partyMemberRepository.save(PartyMember.of(party, applicant));
							party.addPartyMember(partyMember);

							if (Math.random() < 0.5) {
								party.updatePartyMemberStatus(applicant, PartyMemberStatus.ACCEPTED);
							}
						});

					return party;
				}))
			.toList();
	}
}
