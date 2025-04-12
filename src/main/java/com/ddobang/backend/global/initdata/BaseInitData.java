package com.ddobang.backend.global.initdata;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.message.entity.Message;
import com.ddobang.backend.domain.message.repository.MessageRepository;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.repository.RegionRepository;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.repository.StoreRepository;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.domain.theme.repository.ThemeRepository;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
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
	private final ThemeStatRepository themeStatRepository;
	private final ThemeTagRepository themeTagRepository;
	private final MemberRepository memberRepository;
	private final DiaryService diaryService;
	private final MessageRepository messageRepository;
	private final AlarmRepository alarmRepository;

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
			self.messageAndAlarmInitData();
		};
	}

	// Member init data
	@Transactional
	public void memberInitData() {
		if (memberRepository.count() > 0) {
			return;
		}

		// 테스트용 회원 생성
		Member member = Member.builder()
			.nickname("testUser1")
			.build();

		memberRepository.save(member);
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
		if (diaryService.count() > 0) {
			return;
		}

		for (int i = 1; i <= 9; i++) {
			diaryService.write(
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

	// 메시지와 알림 샘플 데이터 추가
	@Transactional
	public void messageAndAlarmInitData() {
		// 이미 샘플 데이터가 있는 경우 실행하지 않음
		if (messageRepository.count() > 0 && alarmRepository.count() > 0) {
			return;
		}

		// 테스트용 회원을 추가 생성
		Member testUser1 = memberRepository.findByNickname("testUser1").orElseGet(() -> {
			Member member = Member.builder()
				.nickname("testUser1")
				.build();
			return memberRepository.save(member);
		});

		// 테스트용 회원 2 생성
		Member testUser2 = Member.builder()
			.nickname("testUser2")
			.build();
		memberRepository.save(testUser2);

		// 테스트용 회원 3 생성
		Member testUser3 = Member.builder()
			.nickname("testUser3")
			.build();
		memberRepository.save(testUser3);

		// 테스트용 관리자 생성
		Member admin = Member.builder()
			.nickname("admin")
			.build();
		memberRepository.save(admin);

		// 메시지 샘플 데이터 10개 생성
		createSampleMessages(testUser1, testUser2, testUser3, admin);

		// 알림 샘플 데이터 10개 생성
		createSampleAlarms(testUser1, testUser2, testUser3);
	}

	// 메시지 샘플 데이터 생성 메소드
	private void createSampleMessages(Member testUser1, Member testUser2, Member testUser3, Member admin) {
		// 1. 유저1이 유저2에게 보낸 메시지 (읽지 않음)
		messageRepository.save(Message.builder()
			.sender(testUser1)
			.receiver(testUser2)
			.content("안녕하세요! 방탈출 같이 하실래요?")
			.isRead(false)
			.build());

		// 2. 유저2가 유저1에게 보낸 답장 (읽음)
		messageRepository.save(Message.builder()
			.sender(testUser2)
			.receiver(testUser1)
			.content("네! 언제 가능하세요?")
			.isRead(true)
			.build());

		// 3. 유저1이 유저2에게 보낸 메시지 (읽지 않음)
		messageRepository.save(Message.builder()
			.sender(testUser1)
			.receiver(testUser2)
			.content("이번 주말에 강남에 있는 '테마 3'은 어떨까요?")
			.isRead(false)
			.build());

		// 4. 유저3이 유저1에게 보낸 메시지 (읽지 않음)
		messageRepository.save(Message.builder()
			.sender(testUser3)
			.receiver(testUser1)
			.content("다른 방탈출 팀원 구하고 있나요?")
			.isRead(false)
			.build());

		// 5. 관리자가 유저1에게 보낸 메시지 (읽음)
		messageRepository.save(Message.builder()
			.sender(admin)
			.receiver(testUser1)
			.content("회원 가입을 축하합니다! 방탈출 일기를 많이 작성해주세요.")
			.isRead(true)
			.build());

		// 6. 유저2가 유저3에게 보낸 메시지 (읽지 않음)
		messageRepository.save(Message.builder()
			.sender(testUser2)
			.receiver(testUser3)
			.content("이번에 나온 신규 테마 재미있다고 하던데 같이 가실래요?")
			.isRead(false)
			.build());

		// 7. 유저1이 유저3에게 보낸 메시지 (읽음)
		messageRepository.save(Message.builder()
			.sender(testUser1)
			.receiver(testUser3)
			.content("네, 저희 팀에 한 명 더 필요해요! 참여하실래요?")
			.isRead(true)
			.build());

		// 8. 유저3이 유저2에게 보낸 메시지 (읽지 않음)
		messageRepository.save(Message.builder()
			.sender(testUser3)
			.receiver(testUser2)
			.content("어떤 테마인가요? 저도 관심있어요.")
			.isRead(false)
			.build());

		// 9. 관리자가 유저2에게 보낸 메시지 (읽음)
		messageRepository.save(Message.builder()
			.sender(admin)
			.receiver(testUser2)
			.content("이벤트에 참여해주셔서 감사합니다! 상품이 발송될 예정입니다.")
			.isRead(true)
			.build());

		// 10. 유저1이 유저2에게 보낸 메시지 (읽지 않음)
		messageRepository.save(Message.builder()
			.sender(testUser1)
			.receiver(testUser2)
			.content("주말에 만나서 자세히 얘기해요!")
			.isRead(false)
			.build());
	}

	// 알림 샘플 데이터 생성 메소드
	private void createSampleAlarms(Member testUser1, Member testUser2, Member testUser3) {
		// 1. 시스템 알림 (읽지 않음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser1)
			.title("시스템 공지사항")
			.content("서비스 업데이트가 있었습니다. 새로운 기능을 확인해보세요.")
			.alarmType(AlarmType.SYSTEM)
			.build());

		// 2. 메시지 알림 (읽지 않음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser1)
			.title("새로운 메시지가 도착했습니다")
			.content("testUser3님으로부터 새로운 메시지가 도착했습니다.")
			.alarmType(AlarmType.MESSAGE)
			.relId(4L) // 메시지 ID 참조
			.build());

		// 3. 구독 알림 (읽지 않음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser1)
			.title("구독 알림")
			.content("관심 테마 '공포'에 새로운 테마가 등록되었습니다.")
			.alarmType(AlarmType.SUBSCRIBE)
			.relId(5L) // 테마 ID 참조
			.build());

		// 4. 모임 신청 알림 (읽음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser2)
			.title("모임 신청 알림")
			.content("testUser1님이 모임에 참여를 신청했습니다.")
			.alarmType(AlarmType.PARTY_APPLY)
			.relId(1L) // 모임 ID 참조
			.build());

		// 5. 모임 상태 변경 알림 (읽지 않음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser1)
			.title("모임 상태 알림")
			.content("신청하신 모임 참여가 승인되었습니다.")
			.alarmType(AlarmType.PARTY_STATUS)
			.relId(1L) // 모임 ID 참조
			.build());

		// 6. 답변 알림 (읽지 않음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser3)
			.title("문의 답변 알림")
			.content("문의하신 글에 답변이 등록되었습니다.")
			.alarmType(AlarmType.ANSWER_COMMENT)
			.relId(2L) // 문의글 ID 참조
			.build());

		// 7. 답변 알림 (읽음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser2)
			.title("문의 답변 알림")
			.content("문의하신 글에 답변이 등록되었습니다.")
			.alarmType(AlarmType.POST_REPLY)
			.relId(3L) // 문의글 ID 참조
			.build());

		// 8. 시스템 알림 (읽지 않음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser2)
			.title("환영합니다!")
			.content("방탈출 커뮤니티에 가입하신 것을 환영합니다.")
			.alarmType(AlarmType.SYSTEM)
			.build());

		// 9. 기타 알림 (읽지 않음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser3)
			.title("이벤트 알림")
			.content("추천 이벤트에 참여하고 혜택을 받아보세요.")
			.alarmType(AlarmType.OTHER)
			.build());

		// 10. 메시지 알림 (읽지 않음)
		alarmRepository.save(Alarm.builder()
			.receiver(testUser2)
			.title("새로운 메시지가 도착했습니다")
			.content("testUser1님으로부터 새로운 메시지가 도착했습니다.")
			.alarmType(AlarmType.MESSAGE)
			.relId(10L) // 메시지 ID 참조
			.build());
	}
}
