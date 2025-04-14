package com.ddobang.backend.support;

import java.math.BigDecimal;
import java.util.List;

import com.ddobang.backend.domain.member.entity.EscapeSummaryStat;
import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberStat;
import com.ddobang.backend.domain.member.entity.MemberTag;
import com.ddobang.backend.domain.member.entity.MemberTagMapping;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.member.repository.MemberStatRepository;
import com.ddobang.backend.domain.member.repository.MemberTagMappingRepository;
import com.ddobang.backend.domain.member.repository.MemberTagRepository;

public class MemberTestFactory {

	// 기본 테스트 멤버 생성 (일반 사용자)
	public static Member Basic() {
		return Member.builder()
			.nickname("테스트닉네임")
			.gender(Gender.BLIND)
			.introduction("자기소개 예시입니다.")
			.kakaoId("kakao1234")
			.profilePictureUrl("https://example.com/profile.jpg")
			.mannerScore(BigDecimal.valueOf(50))
			.hostCount(50)
			.admPassword(null)
			.build();
	}

	// 카카오ID만 주입
	public static Member withKakaoId(String kakaoId) {
		return Member.builder()
			.nickname("테스트닉네임")
			.gender(Gender.BLIND)
			.introduction("자기소개 예시입니다.")
			.kakaoId(kakaoId)
			.profilePictureUrl("https://example.com/profile.jpg")
			.mannerScore(BigDecimal.valueOf(50))
			.hostCount(50)
			.admPassword(null)
			.build();
	}

	// 닉네임만 주입
	public static Member withNickname(String nickname) {
		return Member.builder()
			.nickname(nickname)
			.gender(Gender.BLIND)
			.introduction("자기소개 예시입니다.")
			.kakaoId("kakao1234")
			.profilePictureUrl("https://example.com/profile.jpg")
			.mannerScore(BigDecimal.valueOf(50))
			.hostCount(50)
			.admPassword(null)
			.build();
	}

	// 기본 멤버 생성 (일반 사용자) + 태그 + 통계(프로필용)
	public static Member fullWithTagsAndStats(
		MemberRepository memberRepository,
		MemberTagRepository tagRepository,
		MemberTagMappingRepository mappingRepository,
		MemberStatRepository statRepository
	) {
		// 기본 멤버 저장
		Member member = memberRepository.save(Basic());

		// 기본 태그 세팅
		List<MemberTag> tags = List.of(
			tagRepository.save(new MemberTag("공포 장르를 좋아해요")),
			tagRepository.save(new MemberTag("노힌트를 선호해요")),
			tagRepository.save(new MemberTag("장치가 많은 테마를 선호해요")),
			tagRepository.save(new MemberTag("스토리 몰입형이에요")),
			tagRepository.save(new MemberTag("탈출 실패하면 며칠 생각나요"))
		);

		// 태그 매핑 저장
		List<MemberTagMapping> mappings = tags.stream()
			.map(tag -> new MemberTagMapping(member, tag))
			.toList();
		mappingRepository.saveAll(mappings);

		// 프로필용 통계만 세팅
		EscapeSummaryStat stat = EscapeSummaryStat.builder()
			.totalCount(50)
			.successRate(0.75)
			.noHintSuccessRate(0.65)
			.build();
		statRepository.save(new MemberStat(member, stat));

		return member;
	}
}
