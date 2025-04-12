package com.ddobang.backend.domain.board.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.ddobang.backend.domain.alarm.repository.AlarmRepository;
import com.ddobang.backend.domain.alarm.dto.response.AlarmResponse;
import com.ddobang.backend.domain.alarm.service.AlarmEventService;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.board.event.PostReplyCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardAlarmListener {

	private final AlarmRepository alarmRepository;
	private final AlarmEventService alarmEventService;
	private final MemberService memberService;

	@EventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handlePostReplyCreatedEvent(PostReplyCreatedEvent event) {
		log.info("문의 답변 이벤트 수신: 문의 ID {}, 작성자 ID {}",
			event.getPostId(), event.getPostOwnerId());

		try {
			// 게시물 작성자 Member 조회
			Member postOwner = memberService.getMemberById(event.getPostOwnerId());
			
			// 내용이 너무 길면 잘라서 미리보기로 만들기
			String previewContent = truncateContent(event.getReplyContent(), 50);
			
			// Alarm 객체 생성 및 저장
			Alarm alarm = Alarm.builder()
				.receiver(postOwner)
				.title("문의하신 글에 답변이 등록되었습니다")
				.content("'" + event.getPostTitle() + "' 문의에 답변이 등록되었습니다: " + previewContent)
				.alarmType(AlarmType.POST_REPLY)  // 문의 답변에 대한 알람 타입
				.relId(event.getPostId())
				.build();
			
			Alarm savedAlarm = alarmRepository.save(alarm);
			AlarmResponse alarmResponse = AlarmResponse.from(savedAlarm);

			// 실시간 알림 전송 (SSE)
			alarmEventService.sendNotification(event.getPostOwnerId(), alarmResponse);

			log.info("문의 답변 알림 생성 및 전송 완료: 알림 ID {}", savedAlarm.getId());
		} catch (Exception e) {
			log.error("문의 답변 알림 생성 중 오류 발생", e);
			// 알림 실패 시에도 답변 기능에는 영향을 주지 않도록 예외를 잡음
		}
	}

	// 긴 내용을 잘라서 미리보기 형태로 만드는 유틸리티 메서드
	private String truncateContent(String content, int maxLength) {
		if (content == null) {
			return "";
		}

		// HTML 태그 제거 (답변이 HTML 형식일 경우)
		String plainText = content.replaceAll("<[^>]*>", "");

		if (plainText.length() <= maxLength) {
			return plainText;
		}

		return plainText.substring(0, maxLength) + "...";
	}
}