package com.ddobang.backend.domain.upload.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ddobang.backend.domain.upload.service.S3UploadService;

import lombok.RequiredArgsConstructor;

/**
 * ImageEventListener
 * <p></p>
 * @author 100minha
 */
@Component
@RequiredArgsConstructor
public class ImageEventListener {

	private final S3UploadService s3UploadService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onProfileImageChanged(ProfileImageChangedEvent event) {
		System.out.println(event.oldUrl());
		s3UploadService.delete(event.oldUrl());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onDiaryImageChanged(DiaryImageChangedEvent event) {
		System.out.println(event.oldUrl());
		s3UploadService.delete(event.oldUrl());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onAttachmentChanged(PostAttachmentsUpdatedEvent event) {
		event.oldUrls().forEach(s3UploadService::delete);
	}
}
