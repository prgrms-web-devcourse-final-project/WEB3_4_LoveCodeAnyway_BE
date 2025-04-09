package com.ddobang.backend.domain.alarm.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.message.event.MessageSentEvent;
import com.ddobang.backend.domain.party.event.PartyCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 알림 이벤트 리스너
 * 다양한 도메인에서 발생하는 이벤트를 구독하여 알림을 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmEventListener {

	private final AlarmService alarmService;

	/**
	 * 메시지 전송 이벤트 처리
	 * 메시지가 전송되면 수신자에게 알림을 생성합니다.
	 */
	@Async("alarmTaskExecutor")
	@TransactionalEventListener
	public void handleMessageSentEvent(MessageSentEvent event) {
		log.info("메시지 전송 이벤트 수신: messageId={}, 수신자={}", event.getMessageId(), event.getReceiverId());

		AlarmCreateRequest request = AlarmCreateRequest.builder()
			.receiverId(event.getReceiverId())
			.title("새 메시지 도착")
			.content(event.getSenderName() + "님으로부터 새 메시지가 도착했습니다.")
			.relId(event.getMessageId())
			.alarmType(AlarmType.MESSAGE)
			.build();

		alarmService.createAlarm(request);

		log.info("메시지 알림 생성 완료: 수신자={}", event.getReceiverId());
	}

	/**
	 * 파티 생성 이벤트 처리
	 * 파티가 생성되면 관련 키워드 구독자들에게 알림을 생성합니다.
	 */
	@Async("alarmTaskExecutor")
	@TransactionalEventListener
	public void handlePartyCreatedEvent(PartyCreatedEvent event) {
		log.info("파티 생성 이벤트 수신: partyId={}, 제목={}", event.getPartyId(), event.getPartyTitle());

		// 실제 구현에서는 키워드 구독자 조회 로직 필요
		// 예시로 키워드별 알림 생성 로직만 구현
		for (String keyword : event.getKeywords()) {
			log.info("키워드 '{}' 관련 알림 처리", keyword);

			// 키워드 구독자 조회 로직 (예시)
			// List<Long> subscriberIds = keywordSubscriberRepository.findSubscriberIdsByKeyword(keyword);
			//
			// subscriberIds.forEach(subscriberId -> {
			//     AlarmCreateRequest request = AlarmCreateRequest.builder()
			//         .receiverId(subscriberId)
			//         .title("관심 키워드 파티 생성")
			//         .content("관심 키워드 '" + keyword + "'에 대한 새 파티가 생성되었습니다: " + event.getPartyTitle())
			//         .relId(event.getPartyId())
			//         .alarmType(AlarmType.SUBSCRIBE)
			//         .build();
			//
			//     alarmService.createAlarm(request);
			// });
		}

		log.info("파티 알림 처리 완료: partyId={}", event.getPartyId());
	}

	// 추가 이벤트 핸들러들...
	// 예: Theme 도메인 등의 이벤트 처리
}
