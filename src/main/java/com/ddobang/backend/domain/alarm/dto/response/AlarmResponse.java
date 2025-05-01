package com.ddobang.backend.domain.alarm.dto.response;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.entity.AlarmType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmResponse {
	private Long id;
	private Long receiverId;
	private String title;
	private String content;
	private Boolean readStatus;
	private AlarmType alarmType;
	private Long relId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime modifiedAt;

	public static AlarmResponse from(Alarm alarm) {
		return AlarmResponse.builder()
			.id(alarm.getId())
			.receiverId(alarm.getReceiverId())
			.title(alarm.getTitle())
			.content(alarm.getContent())
			.readStatus(alarm.getReadStatus())
			.alarmType(alarm.getAlarmType())
			.relId(alarm.getRelId())
			.createdAt(alarm.getCreatedAt())
			.modifiedAt(alarm.getModifiedAt())
			.build();
	}
}