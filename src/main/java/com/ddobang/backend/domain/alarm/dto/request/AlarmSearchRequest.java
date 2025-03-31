package com.ddobang.backend.domain.alarm.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.ddobang.backend.domain.alarm.entity.AlarmType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmSearchRequest { // 알림 종류별로 조회시 분류용
	private AlarmType alarmType;
	private Boolean readStatus;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endDate;
}