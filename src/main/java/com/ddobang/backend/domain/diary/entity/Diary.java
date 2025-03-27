package com.ddobang.backend.domain.diary.entity;

import java.time.LocalDate;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Diary extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "theme_id", nullable = false)
	// private Theme theme;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "member_id", nullable = false)
	// private Member author;

	@OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
	private DiaryStats diaryStats;

	private LocalDate escapeDate;
	private String imageUrl;
	private String participants;

	@Column(columnDefinition = "TEXT")
	private String review;

	@Builder
	public Diary(
		//Theme theme,
		LocalDate escapeDate,
		String imageUrl,
		String participants,
		String review
	) {
		//this.theme = theme;
		this.escapeDate = escapeDate;
		this.imageUrl = imageUrl;
		this.participants = participants;
		this.review = review;
	}

	public void setDiaryStats(DiaryStats diaryStats) {
		this.diaryStats = diaryStats;
	}
}
