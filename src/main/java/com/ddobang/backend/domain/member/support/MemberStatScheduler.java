package com.ddobang.backend.domain.member.support;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.entity.MemberStat;
import com.ddobang.backend.domain.member.repository.MemberStatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStatScheduler {
	private final MemberStatRepository memberStatRepository;
	private final MemberStatCalculator memberStatCalculator;

	// 방탈출을 시작한 날부터 0000일
	// 매일 0시에 반영
	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void updateDaysSinceFirstEscape() {
		int updatedCount = memberStatRepository.incrementDaysSinceFirstEscape();

		log.info("{}개의 daysSinceFirstEscape 값이 반영 되었습니다.", updatedCount);
	}

	// 월별 데이터 수정
	// 매달 1일 0시에 반영
	@Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
	@Transactional
	public void updateMonthlyMemberStat() {
		List<MemberStat> memberStats = memberStatRepository.findAll();

		for (MemberStat memberStat : memberStats) {
			memberStatCalculator.upDateEscapeScheduleStat(memberStat);
		}

		List<MemberStat> updated = memberStatRepository.saveAll(memberStats);

		log.info("{}개의 사용자의 월별 분석 추이, 이번달 관련 데이터들이 반영되었습니다.", updated.size());
	}
}
