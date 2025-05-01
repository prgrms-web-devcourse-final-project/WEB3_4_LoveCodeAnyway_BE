package com.ddobang.backend.domain.alarm.event;

import com.ddobang.backend.global.event.DomainEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmEvent implements DomainEvent {
	private Long receiverId;
	private String title;
	private String content;
	private Long relId;
	private String alarmType;

	@Override
	public String getEventType() {
		return "ALARM_EVENT";
	}
}