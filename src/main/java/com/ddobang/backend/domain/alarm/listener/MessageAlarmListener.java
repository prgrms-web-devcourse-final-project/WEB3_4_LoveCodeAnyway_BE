package com.ddobang.backend.domain.alarm.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest;
import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.message.event.MessageCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageAlarmListener {

	private final AlarmService alarmService;
	private final AlarmEventService alarmEventService;

	@EventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW) // 별도 트랜잭션으로 처리
	public void handleMessageCreatedEvent(MessageCreatedEvent event) {
		log.info("메시지 생성 이벤트 수신: 발신자 {}, 수신자 {}",
			event.getSenderNickname(), event.getReceiverNickname());

		// 알림 생성 요청 객체 구성
		AlarmCreateRequest alarmRequest = AlarmCreateRequest.builder()
			.receiverId(event.getReceiverId())
			.title("새 쪽지가 도착했습니다.")
			.content(event.getSenderNickname() + "님으로부터 쪽지가 도착했습니다.")
			.alarmType(AlarmType.MESSAGE)
			.relId(event.getMessageId())
			.build();

		try {
			// 알림 생성
			AlarmResponse createdAlarm = alarmService.createAlarm(alarmRequest);

			// 실시간 알림 전송 (SSE)
			alarmEventService.sendNotification(event.getReceiverId(), createdAlarm);

			log.info("메시지 알림 생성 및 전송 완료: 알림 ID {}", createdAlarm.getId());
		} catch (Exception e) {
			log.error("메시지 알림 생성 중 오류 발생", e);
			// 알림 실패 시에도 메시지 기능에는 영향을 주지 않도록 예외를 잡음
		}
	}
}