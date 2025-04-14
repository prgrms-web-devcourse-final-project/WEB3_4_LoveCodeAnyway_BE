package com.ddobang.backend.global.initdata;

import java.math.BigDecimal;
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

import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.repository.AlarmRepository;
import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.service.DiaryService;
import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.message.entity.Message;
import com.ddobang.backend.domain.message.repository.MessageRepository;
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
	private final MessageRepository messageRepository;  // 추가
	private final AlarmRepository alarmRepository; // 추가

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
			self.messageInitData();  // 추가
			self.alarmInitData();    // 추가
		};
	}

	// Member init data
	@Transactional
	public void memberInitData() {
		if (memberRepository.count() > 0) {
			return;
		}

		// TODO: 테스트용 회원 생성 - 추후 삭제 예정 or 변경
		Member member1 = Member.builder()
			.nickname("testUser1")
			.build();

		Member member2 = Member.builder()
			.nickname("testUser2")
			.build();

		// 추가 회원
		Member member3 = Member.builder()
			.nickname("testUser3")
			.gender(Gender.MALE)
			.introduction("공포 테마 전문가입니다.")
			.mannerScore(BigDecimal.valueOf(85))
			.hostCount(3)
			.build();

		Member member4 = Member.builder()
			.nickname("testUser4")
			.gender(Gender.FEMALE)
			.introduction("감성 테마를 좋아합니다.")
			.mannerScore(BigDecimal.valueOf(90))
			.hostCount(1)
			.build();

		Member member5 = Member.builder()
			.nickname("testUser5")
			.gender(Gender.MALE)
			.introduction("판타지 테마 애호가입니다.")
			.mannerScore(BigDecimal.valueOf(75))
			.hostCount(0)
			.build();

		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
		memberRepository.save(member4);
		memberRepository.save(member5);
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
		themes = IntStream.range(1, 31)
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
			.map(theme -> {
				// 1. 랜덤 호스트 선택
				Member host = members.get((int)(Math.random() * members.size()));

				// 2. 파티 생성
				PartyRequest request = new PartyRequest(
					theme.getId(),
					theme.getName() + "모임",
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
			})
			.toList();
	}

	// 메시지 초기 데이터 추가 - 완전히 새로운 메서드
	@Transactional
	public void messageInitData() {
		if (messageRepository.count() > 0) {
			return;
		}

		// 이미 생성된 회원 목록 가져오기
		List<Member> members = memberRepository.findAll();

		// 최소 5명의 회원이 필요하므로 부족하면 추가 생성
		if (members.size() < 5) {
			int membersToAdd = 5 - members.size();
			for (int i = 0; i < membersToAdd; i++) {
				String nickname = "messageUser" + i;
				members.add(memberRepository.save(Member.builder()
					.nickname(nickname)
					.gender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE)
					.introduction("메시지 테스트용 회원 " + i)
					.mannerScore(BigDecimal.valueOf(70 + i * 5))
					.hostCount(i)
					.build()));
			}
		}

		List<Message> messages = new ArrayList<>();

		// 회원들 간의 다양한 메시지 대화 생성 (약 30개)
		// 1. 회원1과 회원2 사이의 대화 (10개)
		Member member1 = members.get(0);
		Member member2 = members.get(1);

		messages.add(createMessage(member1, member2, "안녕하세요! 방탈출 같이 하실래요?", false));
		messages.add(createMessage(member2, member1, "네, 좋아요! 어떤 테마 생각하고 계신가요?", true));
		messages.add(createMessage(member1, member2, "강남쪽 공포 테마 생각하고 있어요.", false));
		messages.add(createMessage(member2, member1, "저도 공포 테마 좋아해요! 언제 시간 괜찮으세요?", true));
		messages.add(createMessage(member1, member2, "이번 주말 어떠세요?", false));
		messages.add(createMessage(member2, member1, "토요일은 약속이 있어서 일요일이 좋을 것 같아요.", true));
		messages.add(createMessage(member1, member2, "일요일 오후 2시 어떠세요?", false));
		messages.add(createMessage(member2, member1, "좋아요! 일요일 오후 2시로 예약해볼게요.", true));
		messages.add(createMessage(member1, member2, "예약 완료했어요. 일요일에 봬요!", false));
		messages.add(createMessage(member2, member1, "네, 기대되네요. 일요일에 봬요!", false));

		// 2. 회원1과 회원3 사이의 대화 (7개)
		Member member3 = members.get(2);

		messages.add(createMessage(member1, member3, "안녕하세요! 프로필 보고 메시지 드려요.", false));
		messages.add(createMessage(member3, member1, "안녕하세요! 무슨 일이신가요?", true));
		messages.add(createMessage(member1, member3, "다음 주에 방탈출 모임을 계획 중인데 참여하실래요?", false));
		messages.add(createMessage(member3, member1, "어떤 테마인가요?", true));
		messages.add(createMessage(member1, member3, "홍대 쪽 판타지 테마입니다.", false));
		messages.add(createMessage(member3, member1, "재밌겠네요! 시간대는 어떻게 되나요?", true));
		messages.add(createMessage(member1, member3, "다음 주 토요일 오후 3시입니다.", false));

		// 3. 회원2와 회원4 사이의 대화 (7개)
		Member member4 = members.get(3);

		messages.add(createMessage(member2, member4, "안녕하세요! 모임 후기 작성하셨나요?", false));
		messages.add(createMessage(member4, member2, "아직이요. 오늘 중으로 작성할게요!", true));
		messages.add(createMessage(member2, member4, "넵, 감사합니다!", false));
		messages.add(createMessage(member4, member2, "후기 작성 완료했어요!", true));
		messages.add(createMessage(member2, member4, "빠른 작성 감사합니다!", false));
		messages.add(createMessage(member4, member2, "다음에 또 같이해요!", true));
		messages.add(createMessage(member2, member4, "네, 좋아요!", false));

		// 4. 회원3과 회원5 사이의 대화 (6개)
		Member member5 = members.get(4);

		messages.add(createMessage(member3, member5, "지난번 모임 즐거웠어요!", false));
		messages.add(createMessage(member5, member3, "저도 정말 즐거웠어요! 다음에 또 해요.", true));
		messages.add(createMessage(member3, member5, "다음 달에 새로운 테마 오픈한대요.", false));
		messages.add(createMessage(member5, member3, "오, 어떤 테마인가요?", true));
		messages.add(createMessage(member3, member5, "미스터리 장르라고 해요. 예약해볼까요?", false));
		messages.add(createMessage(member5, member3, "좋아요! 예약 가능하면 알려주세요!", false));

		// 저장
		messageRepository.saveAll(messages);
	}

	private Message createMessage(Member sender, Member receiver, String content, boolean isRead) {
		return Message.builder()
			.sender(sender)
			.receiver(receiver)
			.content(content)
			.isRead(isRead)
			.build();
	}

	// 알림 초기 데이터 추가 - 완전히 새로운 메서드
	@Transactional
	public void alarmInitData() {
		if (alarmRepository.count() > 0) {
			return;
		}

		List<Member> members = memberRepository.findAll();
		List<Alarm> alarms = new ArrayList<>();

		// 메시지 관련 알림 (5개)
		alarms.add(createAlarm(members.get(0).getId(), "새 쪽지가 도착했습니다.",
			members.get(1).getNickname() + "님으로부터 쪽지가 도착했습니다.",
			AlarmType.MESSAGE, 1L, false));

		alarms.add(createAlarm(members.get(1).getId(), "새 쪽지가 도착했습니다.",
			members.get(0).getNickname() + "님으로부터 쪽지가 도착했습니다.",
			AlarmType.MESSAGE, 2L, true));

		alarms.add(createAlarm(members.get(2).getId(), "새 쪽지가 도착했습니다.",
			members.get(0).getNickname() + "님으로부터 쪽지가 도착했습니다.",
			AlarmType.MESSAGE, 3L, false));

		alarms.add(createAlarm(members.get(3).getId(), "새 쪽지가 도착했습니다.",
			members.get(2).getNickname() + "님으로부터 쪽지가 도착했습니다.",
			AlarmType.MESSAGE, 4L, true));

		alarms.add(createAlarm(members.get(4).getId(), "새 쪽지가 도착했습니다.",
			members.get(3).getNickname() + "님으로부터 쪽지가 도착했습니다.",
			AlarmType.MESSAGE, 5L, false));

		// 모임 관련 알림 (5개)
		alarms.add(createAlarm(members.get(0).getId(), "새로운 모임 참가 신청이 있습니다",
			members.get(2).getNickname() + "님이 '테마 1모임_0' 모임에 참가를 신청했습니다.",
			AlarmType.PARTY_APPLY, 1L, false));

		alarms.add(createAlarm(members.get(1).getId(), "모임 참가 신청이 승인되었습니다",
			"'테마 2모임_1' 모임의 참가 신청이 " + members.get(0).getNickname() + " 모임장에 의해 승인되었습니다.",
			AlarmType.PARTY_STATUS, 2L, true));

		alarms.add(createAlarm(members.get(2).getId(), "새로운 모임 참가 신청이 있습니다",
			members.get(3).getNickname() + "님이 '테마 3모임_0' 모임에 참가를 신청했습니다.",
			AlarmType.PARTY_APPLY, 3L, false));

		alarms.add(createAlarm(members.get(3).getId(), "모임 참가 신청이 거절되었습니다",
			"'테마 4모임_1' 모임의 참가 신청이 " + members.get(1).getNickname() + " 모임장에 의해 거절되었습니다.",
			AlarmType.PARTY_STATUS, 4L, true));

		alarms.add(createAlarm(members.get(4).getId(), "새로운 모임 참가 신청이 있습니다",
			members.get(0).getNickname() + "님이 '테마 5모임_2' 모임에 참가를 신청했습니다.",
			AlarmType.PARTY_APPLY, 5L, false));

		// 문의 답변 알림 (3개)
		alarms.add(createAlarm(members.get(0).getId(), "문의하신 글에 답변이 등록되었습니다",
			"'방탈출 예약 문의' 문의에 답변이 등록되었습니다: 안녕하세요. 문의 주셔서 감사합니다...",
			AlarmType.POST_REPLY, 1L, false));

		alarms.add(createAlarm(members.get(1).getId(), "문의하신 글에 답변이 등록되었습니다",
			"'환불 문의' 문의에 답변이 등록되었습니다: 환불 규정에 따라 처리해드리겠습니다...",
			AlarmType.POST_REPLY, 2L, true));

		alarms.add(createAlarm(members.get(2).getId(), "문의하신 글에 답변이 등록되었습니다",
			"'테마 추천 문의' 문의에 답변이 등록되었습니다: 취향에 맞는 테마를 추천해드립니다...",
			AlarmType.POST_REPLY, 3L, false));

		// 시스템 알림 (3개)
		alarms.add(createAlarm(members.get(0).getId(), "시스템 알림",
			"새로운 방탈출 테마가 추가되었습니다! 지금 확인해보세요.",
			AlarmType.SYSTEM, null, true));

		alarms.add(createAlarm(members.get(1).getId(), "시스템 알림",
			"회원님의 계정 정보가 업데이트되었습니다.",
			AlarmType.SYSTEM, null, false));

		alarms.add(createAlarm(members.get(2).getId(), "시스템 알림",
			"새로운 기능이 추가되었습니다! 지금 확인해보세요.",
			AlarmType.SYSTEM, null, true));

		// 저장
		alarmRepository.saveAll(alarms);
	}

	private Alarm createAlarm(Long receiverId, String title, String content,
		AlarmType alarmType, Long relId, boolean isRead) {
		Alarm alarm = Alarm.builder()
			.receiverId(receiverId)
			.title(title)
			.content(content)
			.alarmType(alarmType)
			.relId(relId)
			.build();

		if (isRead) {
			alarm.markAsRead();
		}

		return alarm;
	}
}
