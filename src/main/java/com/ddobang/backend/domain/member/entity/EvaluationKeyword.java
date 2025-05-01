package com.ddobang.backend.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EvaluationKeyword {
	ATTENDANCE("시간 약속을 잘 지켜요"),
	COMMUNICATION("의사소통이 원활해요"),
	COOPERATION("협동을 잘해요"),
	INTUITION("직관력이 좋아요"),
	LEADERSHIP("모임을 잘 이끌어요"),

	LATE("약속 시간을 가끔 놓쳐요"),
	PASSIVE("조금 소극적인 편이에요"),
	SELF_CENTERED("혼자 몰입할 때가 있어요"),
	OFF_TOPIC("산으로 가는 경우가 있어요"),
	RUDE("조금 무뚝뚝할 때가 있어요");

	private final String displayName;
}
