package com.ddobang.backend.domain.member.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.entity.MemberStat;

import jakarta.persistence.EntityManager;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class MemberStatRepositoryTest {
	@Autowired
	private MemberStatRepository memberStatRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("방탈출을 시작한지 0000일, 매일 자정에 돌아가는 리포지터리 메서드 단위 테스트")
	public void t1() {
		int daysSinceFirstEscape = memberStatRepository.findById(1L).orElseThrow()
			.getEscapeSummaryStat().getDaysSinceFirstEscape();

		// 쿼리 실행
		memberStatRepository.incrementDaysSinceFirstEscape();

		entityManager.flush();
		entityManager.clear();

		MemberStat memberStat = entityManager.find(MemberStat.class, 1L);

		// days_since_first_escape가 1 증가했는지 확인
		assertEquals(daysSinceFirstEscape + 1,
			memberStat.getEscapeSummaryStat().getDaysSinceFirstEscape());
	}
}
