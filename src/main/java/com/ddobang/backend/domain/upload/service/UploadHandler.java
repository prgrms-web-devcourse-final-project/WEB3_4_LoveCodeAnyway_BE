package com.ddobang.backend.domain.upload.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ddobang.backend.domain.board.entity.Attachment;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.service.BoardService;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.service.DiaryService;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.upload.event.DiaryImageChangedEvent;
import com.ddobang.backend.domain.upload.event.PostAttachmentsUpdatedEvent;
import com.ddobang.backend.domain.upload.event.ProfileImageChangedEvent;
import com.ddobang.backend.domain.upload.exception.UploadErrorCode;
import com.ddobang.backend.domain.upload.exception.UploadException;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;
import com.ddobang.backend.global.event.EventPublisher;

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

	private final EventPublisher publisher;

	@Transactional
	public void applyImage(FileUploadTarget target, Member member, Long diaryId, String imageUrl) {
		switch (target) {
			case PROFILE -> {
				String oldImageUrl = member.getProfilePictureUrl();

				member.setProfilePictureUrl(imageUrl);
				if (StringUtils.hasText(oldImageUrl)) {
					publisher.publish(new ProfileImageChangedEvent(oldImageUrl));
				}
			}

			case DIARY -> {
				Diary diary = diaryService.findById(diaryId);
				String oldImageUrl = diary.getImageUrl();

				diary.setImageUrl(imageUrl);
				if (StringUtils.hasText(oldImageUrl)) {
					publisher.publish(new DiaryImageChangedEvent(oldImageUrl));
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
			publisher.publish(new PostAttachmentsUpdatedEvent(urls));
		}
	}

}
