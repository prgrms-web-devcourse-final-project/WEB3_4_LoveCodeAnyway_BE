package com.ddobang.backend.domain.member.entity;

import com.ddobang.backend.domain.member.dto.stat.EscapeProfileStatDto;
import com.ddobang.backend.domain.member.dto.stat.EscapeScheduleStatDto;
import com.ddobang.backend.domain.member.dto.stat.EscapeSummaryStatDto;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberStat {
	@Id
	private Long id;

	@Version
	private Long version;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	private Member member;

	@Embedded
	private EscapeSummaryStat escapeSummaryStat;

	@Embedded
	private EscapeProfileStat escapeProfileStat;

	@Embedded
	private EscapeScheduleStat escapeScheduleStat;

	@Builder
	public MemberStat(
		Member member,
		EscapeSummaryStat escapeSummaryStat,
		EscapeProfileStat escapeProfileStat,
		EscapeScheduleStat escapeScheduleStat
	) {
		this.member = member;
		this.escapeSummaryStat = escapeSummaryStat;
		this.escapeProfileStat = escapeProfileStat;
		this.escapeScheduleStat = escapeScheduleStat;
	}

	public void update(
		EscapeSummaryStatDto escapeSummaryStatDto,
		EscapeProfileStatDto escapeProfileStatDto,
		EscapeScheduleStatDto escapeScheduleStatDto
	) {
		escapeSummaryStat.update(escapeSummaryStatDto);
		escapeProfileStat.update(escapeProfileStatDto);
		escapeScheduleStat.update(escapeScheduleStatDto);
	}

	public MemberStat(Member member, EscapeSummaryStat escapeSummaryStat) {
		this.member = member;
		this.escapeSummaryStat = escapeSummaryStat;
	}
}
