package com.ddobang.backend.domain.alarm.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmCountResponse { // 알람갯수
	private Long totalCount;
	private Long unreadCount;

	public static AlarmCountResponse of(Long totalCount, Long unreadCount) {
		return AlarmCountResponse.builder()
			.totalCount(totalCount)
			.unreadCount(unreadCount)
			.build();
	}
}