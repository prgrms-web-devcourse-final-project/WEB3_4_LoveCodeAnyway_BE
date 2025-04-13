package com.ddobang.backend.domain.message;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.repository.AlarmRepository;
import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.message.dto.MessageDto;
import com.ddobang.backend.domain.message.service.MessageService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MessageAlarmIntegrationTest {

	@Autowired
	private MessageService messageService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AlarmRepository alarmRepository;

	@Test
	@DisplayName("메시지 전송 시 수신자에게 알림이 생성되어야 한다")
	void t1() {
		// Given
		Member sender = createMember("보낸사람");
		Member receiver = createMember("받는사람");
		memberRepository.saveAll(List.of(sender, receiver));

		String messageContent = "테스트 메시지입니다.";

		// When
		MessageDto messageDto = messageService.sendMessage(sender, receiver, messageContent);

		// Then
		// 비동기 이벤트 처리 대기
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			List<Alarm> alarms = alarmRepository.findByReceiverIdOrderByCreatedAtDesc(receiver.getId(),
				Pageable.unpaged()).getContent();

			assertThat(alarms).isNotEmpty();
			Alarm alarm = alarms.get(0);

			assertThat(alarm.getReceiverId()).isEqualTo(receiver.getId());
			assertThat(alarm.getTitle()).isEqualTo("새 쪽지가 도착했습니다.");
			assertThat(alarm.getContent()).contains(sender.getNickname());
			assertThat(alarm.getAlarmType()).isEqualTo(AlarmType.MESSAGE);
			assertThat(alarm.getRelId()).isEqualTo(messageDto.getId());
			assertThat(alarm.getReadStatus()).isFalse();
		});
	}

	private Member createMember(String nickname) {
		return Member.builder()
			.nickname(nickname)
			.gender(Gender.MALE)
			.mannerScore(BigDecimal.valueOf(50))
			.hostCount(0)
			.build();
	}
}
