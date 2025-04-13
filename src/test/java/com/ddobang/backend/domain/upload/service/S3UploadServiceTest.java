package com.ddobang.backend.domain.upload.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ddobang.backend.domain.board.dto.request.PostRequest;
import com.ddobang.backend.domain.board.entity.Attachment;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.types.PostType;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.upload.exception.UploadErrorCode;
import com.ddobang.backend.domain.upload.exception.UploadException;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;
import com.ddobang.backend.global.security.LoginMemberProvider;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * S3UploadServiceTest
 * <p></p>
 * @author 100minha
 */
@ExtendWith(MockitoExtension.class)
class S3UploadServiceTest {

	@InjectMocks
	private S3UploadService s3UploadService;

	@Mock
	private S3Client s3Client;

	@Mock
	private UploadHandler uploadHandler;

	@Mock
	private LoginMemberProvider loginMemberProvider;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket = "test-bucket";

	@Value("${cloud.aws.region.static}")
	private String region = "ap-northeast-2";

	private Member testMember;

	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(s3UploadService, "bucket", bucket);
		ReflectionTestUtils.setField(s3UploadService, "region", region);

		testMember = Member.builder()
			.nickname("tester")
			.build();
	}

	@Test
	@DisplayName("빈 파일에 대한 업로드 요청 테스트")
	void t1_1() throws IOException {
		// given
		when(loginMemberProvider.getCurrentMember()).thenReturn(testMember);
		MultipartFile emptyFile = new MockMultipartFile("file", "", "image/png", new byte[0]);

		// when
		s3UploadService.uploadImage(1L, FileUploadTarget.PROFILE, emptyFile);

		// then
		verify(uploadHandler).applyImage(eq(FileUploadTarget.PROFILE), eq(testMember), eq(1L), isNull());
	}

	@Test
	@DisplayName("정상적인 파일 업로드 요청 테스트")
	void t1_2() throws IOException {
		// given
		when(loginMemberProvider.getCurrentMember()).thenReturn(testMember);
		MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());

		// when
		s3UploadService.uploadImage(1L, FileUploadTarget.PROFILE, file);

		// then
		verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
		verify(uploadHandler).applyImage(eq(FileUploadTarget.PROFILE), eq(testMember), eq(1L),
			contains(".amazonaws.com/"));
	}

	@Test
	@DisplayName("잘못된 파일 확장자 업로드 요청 테스트")
	void t1_3() {
		// given
		when(loginMemberProvider.getCurrentMember()).thenReturn(testMember);
		MultipartFile file = new MockMultipartFile("file", "test.exe", "application/octet-stream", "binary".getBytes());

		// when
		UploadErrorCode errorCode = UploadErrorCode.UPLOAD_FILE_INVALID_EXTENSION;

		// then
		assertThatThrownBy(() -> {
			s3UploadService.uploadImage(1L, FileUploadTarget.PROFILE, file);
		}).isInstanceOf(UploadException.class)
			.hasMessageContaining(errorCode.getMessage());
	}

	@Test
	@DisplayName("정상적인 첨부파일 업로드 요청 테스트")
	void t2_1() throws IOException {
		// given
		when(loginMemberProvider.getCurrentMember()).thenReturn(testMember);
		MultipartFile file1 = new MockMultipartFile("file", "a.png", "image/png", "123".getBytes());
		MultipartFile file2 = new MockMultipartFile("file", "b.jpg", "image/jpeg", "456".getBytes());

		PostRequest postRequest = new PostRequest(PostType.REPORT, "test", "test_content", null);
		Post mockPost = Post.of(postRequest, testMember);
		when(uploadHandler.getPostById(anyLong())).thenReturn(mockPost);

		// when
		s3UploadService.uploadAttachment(1L, new MultipartFile[] {file1, file2});

		// then
		verify(uploadHandler).clearAttachmentsByPostId(1L);
		verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
		verify(uploadHandler, times(2)).applyAttachment(eq(mockPost), any(Attachment.class));
	}

	@Test
	@DisplayName("정상적인 url로 s3 객체 삭제 테스트")
	void t3_1() {
		// given
		String url = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/profile/test.jpg";

		// when
		s3UploadService.delete(url);

		// then
		verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	@DisplayName("잘못된 url로 s3 객체 삭제 테스트")
	void t3_2() {
		// given
		String url = "https://malicious.com/hack.png";

		// when
		s3UploadService.delete(url);

		// then
		verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	@DisplayName("공개 URL에서 키 추출 테스트")
	void t4_1() {
		// given
		String url = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/profile/img.png";
		// when
		String key = ReflectionTestUtils.invokeMethod(s3UploadService, "extractKeyFromUrl", url);
		// then
		assertThat("profile/img.png").isEqualTo(key);
	}

	@Test
	@DisplayName("잘못된 URL에서 키 추출 테스트")
	void t4_2() {
		// given
		String url = "https://other-bucket.com/hack.png";
		// when
		String key = ReflectionTestUtils.invokeMethod(s3UploadService, "extractKeyFromUrl", url);
		// then
		assertThat(key.isBlank()).isTrue();
	}
}