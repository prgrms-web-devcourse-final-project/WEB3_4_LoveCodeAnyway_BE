package com.ddobang.backend.domain.upload.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ddobang.backend.domain.board.entity.Attachment;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.upload.exception.UploadErrorCode;
import com.ddobang.backend.domain.upload.exception.UploadException;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;
import com.ddobang.backend.global.security.LoginMemberProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * S3Uploader
 * <p></p>
 * @author 100minha
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

	private final S3Client s3Client;

	private final UploadHandler uploadHandler;
	private final LoginMemberProvider loginMemberProvider;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpeg", "jpg", "gif", "png", "svg", "webp");

	public void uploadImage(long diaryId, FileUploadTarget target, MultipartFile file) throws IOException {

		if (target == FileUploadTarget.NONE) {
			throw new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_TARGET);
		}

		Member member = loginMemberProvider.getCurrentMember();

		if (file == null || file.isEmpty()) {
			uploadHandler.applyImage(target, member, diaryId, null);
			return;
		}
		Long memberId = member.getId();

		String fileName = validAndGenFileName(memberId, target, file);
		putS3Object(fileName, file);

		String publicUrl = getPublicUrl(fileName);
		uploadHandler.applyImage(target, member, diaryId, publicUrl);
	}

	public void uploadAttachment(long postId, MultipartFile[] files) throws IOException {

		uploadHandler.clearAttachmentsByPostId(postId);

		if (files.length == 0) {
			return;
		}

		Member member = loginMemberProvider.getCurrentMember();
		Long memberId = member.getId();
		Post post = uploadHandler.getPostById(postId);

		for (MultipartFile multipartFile : files) {
			String fileName = validAndGenFileName(memberId, FileUploadTarget.POST, multipartFile);
			String originalName = multipartFile.getOriginalFilename();
			putS3Object(fileName, multipartFile);

			String publicUrl = getPublicUrl(fileName);
			Attachment attachment = Attachment.builder()
				.fileName(originalName)
				.url(publicUrl)
				.build();

			uploadHandler.applyAttachment(post, attachment);
		}
	}

	public void delete(String url) {
		String key = extractKeyFromUrl(url);
		if (!StringUtils.hasText(key)) {
			return;
		}

		DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.build();

		s3Client.deleteObject(deleteRequest);
	}

	/*
	업로드를 시도한 파일의 확장자의 유효성을 검사하고, S3에 업로드할 파일 이름을 생성합니다.
	 */
	private String validAndGenFileName(Long memberId, FileUploadTarget target, MultipartFile file) {

		String originalName = file.getOriginalFilename();
		if (originalName == null || originalName.trim().isEmpty() || !originalName.contains(".")) {
			throw new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_FILE_NAME);
		}

		String ext = Optional.of(originalName)
			.filter(n -> n.contains("."))
			.map(n -> n.substring(n.lastIndexOf(".") + 1))
			.orElseThrow(() -> new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_EXTENSION));

		if (!ALLOWED_EXTENSIONS.contains(ext)) {
			throw new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_EXTENSION);
		}

		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));

		return target.getType() + "/" + memberId + "/" + date + "/" +
			UUID.randomUUID() + "_" + originalName;
	}

	// S3에 업로드할 파일을 요청 body로  변환하고 업로드 요청을 보냅니다
	private void putS3Object(String fileName, MultipartFile file) throws IOException {
		PutObjectRequest putRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(fileName)
			.contentType(file.getContentType())
			.build();

		s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
	}

	private String getPublicUrl(String key) {
		return getBucketDomain() + key;
	}

	private String extractKeyFromUrl(String url) {
		String bucketDomain = getBucketDomain();
		if (!url.startsWith(bucketDomain)) {
			log.warn("사진 삭제 중 잘못된 URL이 전달되었습니다. URL: {}", url);
			return "";
		}
		return url.substring(bucketDomain.length());
	}

	private String getBucketDomain() {
		return String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
	}
}
