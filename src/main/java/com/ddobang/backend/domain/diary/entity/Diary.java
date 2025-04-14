package com.ddobang.backend.domain.diary.entity;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.exception.DiaryErrorCode;
import com.ddobang.backend.domain.diary.exception.DiaryException;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id", nullable = false)
	private Theme theme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	private Member author;

	@OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
	private DiaryStat diaryStat;

	private String imageUrl;
	private String participants;

	@Column(columnDefinition = "TEXT")
	private String review;

	@Builder
	public Diary(
		Theme theme,
		Member author,
		String imageUrl,
		String participants,
		String review
	) {
		this.theme = theme;
		this.author = author;
		this.imageUrl = imageUrl;
		this.participants = participants;
		this.review = review;
	}

	public static Diary toDiary(Member author, Theme theme, DiaryRequestDto dto) {
		return Diary.builder()
			.theme(theme)
			.author(author)
			.participants(dto.participants())
			.review(dto.review())
			.build();
	}

	public void checkActor(Member actor) {
		if (!actor.equals(this.getAuthor()))
			throw new DiaryException(DiaryErrorCode.DIARY_FORBIDDEN);
	}

	public void setDiaryStat(DiaryStat diaryStat) {
		this.diaryStat = diaryStat;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void modify(
		Theme theme,
		DiaryRequestDto diaryRequestDto
	) {
		this.theme = theme;
		this.participants = diaryRequestDto.participants();
		this.review = diaryRequestDto.review();
	}
}
