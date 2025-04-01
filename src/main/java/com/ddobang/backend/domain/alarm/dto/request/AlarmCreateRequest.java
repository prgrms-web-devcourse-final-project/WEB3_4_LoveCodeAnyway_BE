package com.ddobang.backend.domain.alarm.dto.request;

import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.alarm.entity.AlarmType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmCreateRequest { // 알림 생성데이터
	@NotNull(message = "수신자 ID는 필수입니다.")
	private Long receiverId;

	@NotBlank(message = "알림 제목은 필수입니다.")
	private String title;

	@NotBlank(message = "알림 내용은 필수입니다.")
	private String content;

	private Long relId;

	@NotNull(message = "알림 타입은 필수입니다.")
	private AlarmType alarmType;

	public Alarm toEntity() {
		return Alarm.builder()
			.receiverId(receiverId)
			.title(title)
			.content(content)
			.relId(relId)
			.alarmType(alarmType)
			.build();
	}
}
