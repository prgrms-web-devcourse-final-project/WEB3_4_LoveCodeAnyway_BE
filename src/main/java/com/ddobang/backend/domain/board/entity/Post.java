package com.ddobang.backend.domain.board.entity;

import java.util.ArrayList;
import java.util.List;

import com.ddobang.backend.domain.board.dto.request.PostRequest;
import com.ddobang.backend.domain.board.types.PostType;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	@Column(name = "answered", nullable = false)
	private boolean answered;

	@NotNull
	@Column(name = "is_deleted", nullable = false)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Attachment> attachments;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostReply> replies;

	private Post(PostRequest postRequest, Member member) {
		this.type = postRequest.type();
		this.title = postRequest.title();
		this.content = postRequest.content();
		this.member = member;
		this.answered = false;
		this.deleted = false;
		this.attachments = postRequest.attachments() == null
			? new ArrayList<>()
			: new ArrayList<>(postRequest.attachments());
		this.replies = new ArrayList<>();
	}

	public static Post of(PostRequest request, Member member) {
		return new Post(request, member);
	}

	public void update(PostRequest postRequest) {
		this.type = postRequest.type();
		this.title = postRequest.title();
		this.content = postRequest.content();
	}

	public void delete() {
		this.deleted = true;
	}

	public void addReply(PostReply reply) {
		this.replies.add(reply);
		this.answered = true;
	}

	public void removeReply(PostReply reply) {
		this.replies.remove(reply);

		if (this.replies.isEmpty()) {
			this.answered = false;
		}
	}

	public void addAttachment(Attachment attachment) {
		this.attachments.add(attachment);
		attachment.setPost(this);
	}
}
