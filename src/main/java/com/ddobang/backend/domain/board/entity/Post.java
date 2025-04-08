package com.ddobang.backend.domain.board.entity;

import com.ddobang.backend.domain.board.dto.request.PostRequest;
import com.ddobang.backend.domain.board.types.PostType;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.global.entity.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Post extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	private PostType type; // QNA, REPORT, THEME

	@NotBlank
	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@NotBlank
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	@NotNull
	@Column(name = "is_deleted", nullable = false)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Attachment> attachments = new ArrayList<>();

	private Post(PostRequest postRequest, Member member) {
		this.type = postRequest.type();
		this.title = postRequest.title();
		this.content = postRequest.content();
		this.member = member;
		this.deleted = false;
		this.attachments = postRequest.attachments();;
	}

	public static Post of(PostRequest request, Member member) {
		return new Post(request, member);
	}

	public void delete() {
		this.deleted = true;
	}
}
