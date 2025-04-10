package com.ddobang.backend.domain.party.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest;
import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.party.event.PartyApplyEvent;
import com.ddobang.backend.domain.party.event.PartyMemberStatusUpdatedEvent;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyMemberStatusListener {

	private final AlarmService alarmService;
	private final AlarmEventService alarmEventService;

	@EventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handlePartyApplyEvent(PartyApplyEvent event) {
		log.info("모임 신청 이벤트 수신: 모임 ID {}, 신청자 {}",
			event.getPartyId(), event.getApplicantNickname());

		// 알림 생성 요청 객체 구성 (모임장에게 알림)
		AlarmCreateRequest alarmRequest = AlarmCreateRequest.builder()
			.receiverId(event.getHostId())
			.title("새로운 모임 참가 신청이 있습니다")
			.content(event.getApplicantNickname() + "님이 '" + event.getPartyTitle() + "' 모임에 참가를 신청했습니다.")
			.alarmType(AlarmType.SUBSCRIBE)  // 적절한 알림 타입 사용
			.relId(event.getPartyId())
			.build();

		try {
			// 알림 생성
			AlarmResponse createdAlarm = alarmService.createAlarm(alarmRequest);

			// 실시간 알림 전송 (SSE)
			alarmEventService.sendNotification(event.getHostId(), createdAlarm);

			log.info("모임 신청 알림 생성 및 전송 완료: 알림 ID {}", createdAlarm.getId());
		} catch (Exception e) {
			log.error("모임 신청 알림 생성 중 오류 발생", e);
			// 알림 실패 시에도 모임 신청 기능에는 영향을 주지 않도록 예외를 잡음
		}
	}

	@EventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handlePartyMemberStatusUpdatedEvent(PartyMemberStatusUpdatedEvent event) {
		log.info("모임 신청 상태 변경 이벤트 수신: 모임 ID {}, 신청자 ID {}, 새 상태 {}",
			event.getPartyId(), event.getMemberId(), event.getNewStatus());

		String title;
		String content;

		// 상태에 따라 알림 내용 분기
		if (event.getNewStatus() == PartyMemberStatus.ACCEPTED) {
			title = "모임 참가 신청이 승인되었습니다";
			content = "'" + event.getPartyTitle() + "' 모임의 참가 신청이 " +
				event.getHostNickname() + " 모임장에 의해 승인되었습니다.";
		} else if (event.getNewStatus() == PartyMemberStatus.CANCELLED) {
			title = "모임 참가 신청이 거절되었습니다";
			content = "'" + event.getPartyTitle() + "' 모임의 참가 신청이 " +
				event.getHostNickname() + " 모임장에 의해 거절되었습니다.";
		} else {
			log.warn("지원하지 않는 상태 변경: {}", event.getNewStatus());
			return;
		}

		// 알림 생성 요청 객체 구성 (신청자에게 알림)
		AlarmCreateRequest alarmRequest = AlarmCreateRequest.builder()
			.receiverId(event.getMemberId())
			.title(title)
			.content(content)
			.alarmType(AlarmType.SUBSCRIBE)  // 적절한 알림 타입 사용
			.relId(event.getPartyId())
			.build();

		try {
			// 알림 생성
			AlarmResponse createdAlarm = alarmService.createAlarm(alarmRequest);

			// 실시간 알림 전송 (SSE)
			alarmEventService.sendNotification(event.getMemberId(), createdAlarm);

			log.info("모임 신청 상태 변경 알림 생성 및 전송 완료: 알림 ID {}", createdAlarm.getId());
		} catch (Exception e) {
			log.error("모임 신청 상태 변경 알림 생성 중 오류 발생", e);
			// 알림 실패 시에도 모임 신청 상태 변경 기능에는 영향을 주지 않도록 예외를 잡음
		}
	}
}