package com.ddobang.backend.domain.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class Attachment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false, length = 512)
	private String url;

	@Column(nullable = false)
	private String fileName;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	private Attachment(
			String url,
			String fileName,
			Post post
	) {
		this.url = url;
		this.fileName = fileName;
		this.post = post;
	}

	public static Attachment of(String url, String fileName, Post post) {
		return new Attachment(url, fileName, post);
	}
}
