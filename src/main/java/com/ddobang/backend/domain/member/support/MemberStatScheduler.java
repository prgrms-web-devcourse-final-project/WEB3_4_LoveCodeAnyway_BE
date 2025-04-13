package com.ddobang.backend.domain.member.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
	// 매달 1일 0시 10분에 반영 (두 번째 스케줄러)
	@Scheduled(cron = "0 10 0 1 * *", zone = "Asia/Seoul")
	@Transactional
	public void updateMonthlyMemberStat() {
		List<MemberStat> memberStats = memberStatRepository.findAll();
		List<MemberStat> updated = new ArrayList<>();
		List<MemberStat> failed = new ArrayList<>();

		for (MemberStat memberStat : memberStats) {
			try {
				memberStatCalculator.upDateEscapeScheduleStat(memberStat);
				updated.add(memberStat);
			} catch (Exception e) {
				log.warn("사용자 {} 통계 업데이트 실패: {}", memberStat.getMember().getId(), e.getMessage());
			}
		}

		try {
			memberStatRepository.saveAll(updated);
		} catch (ObjectOptimisticLockingFailureException e) {
			log.warn("사용자 분석 데이터 스케쥴링 중 낙관적 락 충돌 발생. 개별 재처리 시작.");

			// 배치 저장 실패 시 개별로 저장 재시도
			for (MemberStat memberStat : updated) {
				try {
					memberStatRepository.save(memberStat);
				} catch (ObjectOptimisticLockingFailureException ex) {
					log.error("개별 재시도 실패 - 사용자 {}", memberStat.getId());
					failed.add(memberStat);
				}
			}
		} catch (Exception ex) {
			log.error("사용자 분석 데이터 스케쥴링 중 예외 발생: {}", ex.getMessage());
		}

		log.info("업데이트 완료. 총 시도: {}, 성공: {}, 충돌로 실패: {}",
			memberStats.size(), updated.size() - failed.size(), failed.size());
	}
}
