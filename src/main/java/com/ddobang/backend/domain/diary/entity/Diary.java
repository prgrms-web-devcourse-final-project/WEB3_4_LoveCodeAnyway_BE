package com.ddobang.backend.domain.diary.entity;

import java.time.LocalDateTime;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;

@Entity
@Getter
public class Diary extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id", nullable = false)
	private Theme theme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member author;

	@OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
	private DiaryStats diaryStats;

	private LocalDateTime escapeDate;
	private String imageUrl;
	private String participants;

	@Column(columnDefinition = "TEXT")
	private String review;

	public Diary(
		Theme theme,
		LocalDateTime escapeDate,
		String imageUrl,
		String participants,
		String review
	) {
		this.theme = theme;
		this.escapeDate = escapeDate;
		this.imageUrl = imageUrl;
		this.participants = participants;
		this.review = review;
	}

	public void setDiaryStats(DiaryStats diaryStats) {
		this.diaryStats = diaryStats;
	}
}
