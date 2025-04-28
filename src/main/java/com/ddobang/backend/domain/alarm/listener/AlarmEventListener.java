package com.ddobang.backend.domain.alarm.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest;
import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.event.AlarmEvent;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.global.event.DomainEvent;
import com.ddobang.backend.global.event.EventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 알람 이벤트 리스너
 * 모든 알람 관련 이벤트를 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmEventListener implements EventListener<DomainEvent> {

    private final AlarmService alarmService;
    private final AlarmEventService alarmEventService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 별도 트랜잭션으로 처리
    public void onEvent(DomainEvent event) {
        if (event instanceof AlarmEvent) {
            handleAlarmEvent((AlarmEvent) event);
        } else {
            log.warn("지원하지 않는 이벤트 타입: {}", event.getClass().getName());
        }
    }

    @Override
    public boolean supportsEventType(String eventType) {
        return "ALARM_EVENT".equals(eventType);
    }

    private void handleAlarmEvent(AlarmEvent event) {
        log.info("알람 이벤트 수신: 수신자={}, 제목={}", event.getReceiverId(), event.getTitle());

        // 알림 생성 요청 객체 구성
        AlarmCreateRequest alarmRequest = AlarmCreateRequest.builder()
            .receiverId(event.getReceiverId())
            .title(event.getTitle())
            .content(event.getContent())
            .alarmType(AlarmType.valueOf(event.getAlarmType()))
            .relId(event.getRelId())
            .build();

        try {
            // 직접 AlarmService로 알림 생성
            AlarmResponse createdAlarm = alarmService.createAlarm(alarmRequest);
            log.info("알림 생성 완료: ID={}, 수신자={}", createdAlarm.getId(), event.getReceiverId());

            // 실시간 알림 전송 (SSE)
            alarmEventService.sendNotification(event.getReceiverId(), createdAlarm);
            log.info("실시간 알림 전송 완료: ID={}, 수신자={}", createdAlarm.getId(), event.getReceiverId());
        } catch (Exception e) {
            log.error("알림 처리 중 오류 발생: 수신자={}, 오류={}", 
                event.getReceiverId(), e.getMessage(), e);
        }
    }
}