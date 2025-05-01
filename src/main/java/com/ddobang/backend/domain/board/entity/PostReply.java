package com.ddobang.backend.domain.board.entity;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostReply extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@NotBlank
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	private PostReply(Post post, String content) {
		this.post = post;
		this.content = content;
	}

	public static PostReply of(Post post, String content) {
		return new PostReply(post, content);
	}

	public void updateContent(String content) {
		this.content = content;
	}
}
