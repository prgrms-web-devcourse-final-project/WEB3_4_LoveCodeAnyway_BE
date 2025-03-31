package com.ddobang.backend.domain.board.entity;

import java.util.ArrayList;
import java.util.List;

import com.ddobang.backend.domain.board.types.PostType;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.global.entity.Attachment;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Post extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	private PostType type;

	@NotBlank
	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@NotBlank
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	@Builder.Default
	@NotNull
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Builder.Default
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Attachment> attachments = new ArrayList<>();

}
