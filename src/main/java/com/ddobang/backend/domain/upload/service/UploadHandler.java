package com.ddobang.backend.domain.upload.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.board.entity.Attachment;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.service.BoardService;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.service.DiaryService;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.upload.event.DiaryImageChangedEvent;
import com.ddobang.backend.domain.upload.event.PostAttachmentsUpdatedEvent;
import com.ddobang.backend.domain.upload.event.ProfileImageChangedEvent;
import com.ddobang.backend.domain.upload.exception.UploadErrorCode;
import com.ddobang.backend.domain.upload.exception.UploadException;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * UploadImageHandler
 * <p></p>
 * @author 100minha
 */
@Service
@RequiredArgsConstructor
public class UploadHandler {

	private final DiaryService diaryService;
	private final BoardService boardService;

	// TODO: 삭제 예정
	private final MemberRepository memberRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public void applyImage(FileUploadTarget target, Long id, String imageUrl) {
		switch (target) {
			case PROFILE -> {
				// TODO: 현재 로그인된 사용자의 엔티티를 가져오는 방법 반영 필요
				Member member = memberRepository.findById(1L).get();
				String oldImageUrl = member.getProfilePictureUrl();

				member.setProfilePictureUrl(imageUrl);
				if (oldImageUrl != null) {
					eventPublisher.publishEvent(new ProfileImageChangedEvent(oldImageUrl));
				}
			}

			case DIARY -> {
				Diary diary = diaryService.findById(id);
				String oldImageUrl = diary.getImageUrl();

				diary.setImageUrl(imageUrl);
				if (oldImageUrl != null) {
					eventPublisher.publishEvent(new DiaryImageChangedEvent(oldImageUrl));
				}
			}

			default -> throw new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_TARGET);
		}
	}

	@Transactional
	public void applyAttachment(Post post, Attachment attachment) {
		post.addAttachment(attachment);
	}

	@Transactional
	public Post getPostById(Long postId) {
		return boardService.getPostById(postId);
	}

	@Transactional
	public void clearAttachmentsByPostId(Long postId) {
		List<String> urls = boardService.getAttachmentUrlsByPostId(postId);
		boardService.clearAttachmentsByPostId(postId);

		if (!urls.isEmpty()) {
			eventPublisher.publishEvent(new PostAttachmentsUpdatedEvent(urls));
		}
	}

}
