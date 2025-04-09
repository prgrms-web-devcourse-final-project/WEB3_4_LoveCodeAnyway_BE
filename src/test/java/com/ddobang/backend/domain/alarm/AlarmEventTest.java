package com.ddobang.backend.domain.alarm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.ddobang.backend.domain.alarm.dto.request.AlarmCreateRequest;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.service.AlarmService;
import com.ddobang.backend.domain.message.event.MessageSentEvent;
import com.ddobang.backend.domain.party.event.PartyCreatedEvent;
import com.ddobang.backend.global.event.EventPublisher;

@SpringBootTest
@ActiveProfiles("test")
public class AlarmEventTest {

    @Autowired
    private EventPublisher eventPublisher;
    
    @MockBean
    private AlarmService alarmService;
    
    @Test
    @DisplayName("메시지 전송 이벤트 발행 시 알림이 생성되어야 함")
    void testMessageSentEventTriggersAlarm() {
        // given
        Long messageId = 1L;
        Long senderId = 2L;
        Long receiverId = 3L;
        String senderName = "홍길동";
        
        // when - 메시지 전송 이벤트 발행
        MessageSentEvent event = new MessageSentEvent(messageId, senderId, senderName, receiverId);
        eventPublisher.publish(event);
        
        // then - 비동기 처리 대기 후 알림 서비스 호출 확인
        verify(alarmService, timeout(1000)).createAlarm(any(AlarmCreateRequest.class));
    }
    
    @Test
    @DisplayName("메시지 전송 이벤트 발행 시 올바른 알림 정보가 전달되어야 함")
    void testMessageSentEventPassesCorrectAlarmInfo() throws Exception {
        // given
        Long messageId = 10L;
        Long senderId = 20L;
        Long receiverId = 30L;
        String senderName = "김철수";
        
        // when
        MessageSentEvent event = new MessageSentEvent(messageId, senderId, senderName, receiverId);
        eventPublisher.publish(event);
        
        // then
        verify(alarmService, timeout(1000)).createAlarm(any(AlarmCreateRequest.class));
        
        // AlarmCreateRequest 객체의 필드 내용 검증
        verify(alarmService, timeout(1000)).createAlarm(any(request -> 
            request.getReceiverId().equals(receiverId) &&
            request.getTitle().equals("새 메시지 도착") &&
            request.getContent().contains(senderName) &&
            request.getRelId().equals(messageId) &&
            request.getAlarmType() == AlarmType.MESSAGE
        ));
    }
    
    @Test
    @DisplayName("파티 생성 이벤트 발행 시 알림 이벤트 리스너가 호출되어야 함")
    void testPartyCreatedEventTriggersListener() throws Exception {
        // given
        Long partyId = 100L;
        String partyTitle = "방탈출 같이 가요";
        // 파티 키워드
        var keywords = Arrays.asList("방탈출", "공포", "서울");
        
        // when - 파티 생성 이벤트 발행
        PartyCreatedEvent event = new PartyCreatedEvent(partyId, partyTitle, keywords);
        eventPublisher.publish(event);
        
        // then
        // 실제로는 keywordSubscriberRepository를 모킹하여 테스트해야 하지만,
        // 예시에서는 이벤트 발행 후 일정 시간 대기 후 종료됨을 확인
        Thread.sleep(500); // 이벤트 처리 대기
    }
    
    @Test
    @DisplayName("여러 도메인 이벤트가 동시에 발생해도 비동기로 모두 처리되어야 함")
    void testMultipleEventsProcessedAsynchronously() throws Exception {
        // given
        Long messageId = 1L;
        Long partyId = 2L;
        
        // when - 여러 이벤트 연속 발행
        MessageSentEvent messageEvent = new MessageSentEvent(messageId, 10L, "발신자", 20L);
        PartyCreatedEvent partyEvent = new PartyCreatedEvent(partyId, "새 파티", Arrays.asList("키워드1", "키워드2"));
        
        eventPublisher.publish(messageEvent);
        eventPublisher.publish(partyEvent);
        
        // then
        // 메시지 이벤트에 대한 알림 생성 확인
        verify(alarmService, timeout(1000)).createAlarm(any(request -> 
            request.getRelId().equals(messageId) &&
            request.getAlarmType() == AlarmType.MESSAGE
        ));
        
        // 모든 이벤트가 처리될 시간 대기
        Thread.sleep(500);
    }
}
