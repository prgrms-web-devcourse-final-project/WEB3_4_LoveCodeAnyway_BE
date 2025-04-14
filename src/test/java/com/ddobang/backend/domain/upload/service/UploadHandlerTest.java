package com.ddobang.backend.domain.upload.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddobang.backend.domain.board.dto.request.PostRequest;
import com.ddobang.backend.domain.board.entity.Attachment;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.service.BoardService;
import com.ddobang.backend.domain.board.types.PostType;
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

/**
 * UploadHandlerTest
 * <p></p>
 * @author 100minha
 */
@ExtendWith(MockitoExtension.class)
public class UploadHandlerTest {
	@InjectMocks
	private UploadHandler uploadHandler;

	@Mock
	private DiaryService diaryService;

	@Mock
	private BoardService boardService;

	@Mock
	private EventPublisher publisher;

	@Test
	@DisplayName("프로필 이미지 업로드 시 이전 이미지가 있으면 이벤트를 발행한다")
	void t1_1() {
		// given
		Member member = Member.builder()
			.profilePictureUrl("https://old-image.png")
			.build();
		String newUrl = "https://new-image.png";

		// when
		uploadHandler.applyImage(FileUploadTarget.PROFILE, member, null, newUrl);

		// then
		assertThat(member.getProfilePictureUrl()).isEqualTo(newUrl);
		verify(publisher).publish(argThat(event ->
			event instanceof ProfileImageChangedEvent &&
				((ProfileImageChangedEvent)event).oldUrl().equals("https://old-image.png")
		));
	}

	@Test
	@DisplayName("방탈출 일지 이미지 업로드 시 이전 이미지가 있으면 이벤트를 발행한다")
	void t1_2() {
		// given
		Long diaryId = 1L;
		Diary diary = Diary.builder()
			.imageUrl("https://old-diary.png")
			.build();
		when(diaryService.findById(diaryId)).thenReturn(diary);
		String newUrl = "https://new-diary.png";

		// when
		uploadHandler.applyImage(FileUploadTarget.DIARY, null, diaryId, newUrl);

		// then
		assertThat(diary.getImageUrl()).isEqualTo(newUrl);
		verify(publisher).publish(argThat(event ->
			event instanceof DiaryImageChangedEvent &&
				((DiaryImageChangedEvent)event).oldUrl().equals("https://old-diary.png")
		));
	}

	@Test
	@DisplayName("이미지 업로드의 target이 유효하지 않으면 예외가 발생한다")
	void t1_3() {
		// given
		Member member = Member.builder().build();

		// when
		UploadErrorCode errorCode = UploadErrorCode.UPLOAD_FILE_INVALID_TARGET;
		// then
		assertThatThrownBy(() ->
			uploadHandler.applyImage(FileUploadTarget.NONE, member, 1L, "https://image.png"))
			.isInstanceOf(UploadException.class)
			.hasMessageContaining(errorCode.getMessage());
	}

	@Test
	@DisplayName("첨부 파일을 post에 추가한다")
	void t2_1() {
		// given
		Member member = Member.builder().build();
		PostRequest postRequest = new PostRequest(PostType.QNA, "test", "test_content", Collections.emptyList());

		Post post = Post.of(postRequest, member);
		Attachment attachment = Attachment.builder().url("url").fileName("file.jpg").build();

		// when
		uploadHandler.applyAttachment(post, attachment);

		// then
		assertThat(post.getAttachments()).contains(attachment);
	}

	@Test
	@DisplayName("postId로 post를 조회한다")
	void t4_1() {
		// given
		Member member = Member.builder().build();
		PostRequest postRequest = new PostRequest(PostType.QNA, "test", "test_content", Collections.emptyList());

		Post post = Post.of(postRequest, member);
		when(boardService.getPostById(1L)).thenReturn(post);

		// when
		Post result = uploadHandler.getPostById(1L);

		// then
		assertThat(result).isEqualTo(post);
	}

	@Test
	@DisplayName("게시글의 기존 첨부파일을 초기화하고 이벤트를 발행한다")
	void t5_1() {
		// given
		Long postId = 1L;
		List<String> urls = List.of("https://a.png", "https://b.jpg");
		when(boardService.getAttachmentUrlsByPostId(postId)).thenReturn(urls);

		// when
		uploadHandler.clearAttachmentsByPostId(postId);

		// then
		verify(boardService).clearAttachmentsByPostId(postId);
		verify(publisher).publish(argThat(event ->
			event instanceof PostAttachmentsUpdatedEvent &&
				((PostAttachmentsUpdatedEvent)event).oldUrls().equals(urls)
		));
	}

	@Test
	@DisplayName("게시글에 기존 첨부파일이 없으면 이벤트를 발행하지 않는다")
	void t5_2() {
		// given
		Long postId = 2L;
		when(boardService.getAttachmentUrlsByPostId(postId)).thenReturn(Collections.emptyList());

		// when
		uploadHandler.clearAttachmentsByPostId(postId);

		// then
		verify(publisher, never()).publish(any());
	}
}
