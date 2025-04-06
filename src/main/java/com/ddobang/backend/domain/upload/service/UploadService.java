package com.ddobang.backend.domain.upload.service;

import static com.ddobang.backend.domain.upload.exception.UploadErrorCode.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.exception.BoardErrorCode;
import com.ddobang.backend.domain.board.exception.BoardException;
import com.ddobang.backend.domain.board.repository.AttachmentRepository;
import com.ddobang.backend.domain.board.repository.BoardRepository;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.exception.DiaryErrorCode;
import com.ddobang.backend.domain.diary.exception.DiaryException;
import com.ddobang.backend.domain.diary.repository.DiaryRepository;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.exception.MemberErrorCode;
import com.ddobang.backend.domain.member.exception.MemberException;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.upload.exception.UploadErrorCode;
import com.ddobang.backend.domain.upload.exception.UploadException;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;
import com.ddobang.backend.global.entity.Attachment;
import com.ddobang.backend.global.util.Ut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {
	private final MemberRepository memberRepository;
	private final DiaryRepository diaryRepository;
	private final BoardRepository boardRepository;
	private final AttachmentRepository attachmentRepository;

	@Value("${custom.fileUpload.dirPath}")
	private String fileDirPath;

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpeg", "jpg", "gif", "png", "svg", "webp");

	@Transactional
	public void upload(
		long parentId,
		FileUploadTarget target,
		MultipartFile[] files
	) throws IOException {
		// 저장할 파일의 타겟이 없다면 예외 처리
		if (target == FileUploadTarget.NONE) {
			throw new UploadException(UploadErrorCode.UPLOAD_FILE_MISSING_TARGET);
		}

		// 저장할 파일 경로 생성
		Path uploadDirPath = Path.of(
			target.getType(),
			String.valueOf(parentId),
			LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))
		);

		for (MultipartFile file : files) {
			String originalName = file.getOriginalFilename();

			// 파일 이름이 없는 경우 예외 처리
			if (originalName == null || originalName.length() < 1) {
				throw new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_FILE_NAME);
			}

			String extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();

			// 허용하지 않는 확장자의 경우 예외 처리
			if (!ALLOWED_EXTENSIONS.contains(extension)) {
				throw new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_EXTENSION);
			}

			String fileName = UUID.randomUUID() + "_" + originalName;
			Path path = uploadDirPath.resolve(fileName);
			Path fullPath = Path.of(fileDirPath).resolve(path);

			// 폴더 없으면 생성
			Files.createDirectories(fullPath.getParent());

			// 실제 저장
			file.transferTo(fullPath.toFile());

			applyUploadPathToDomain(target, parentId, path, originalName);
		}
	}

	@Transactional
	public void delete(long id, FileUploadTarget target) throws IOException {
		String path = "";

		switch (target) {
			case PROFILE -> {
				Member member = memberRepository.findById(id)
					.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

				path = member.getProfilePictureUrl();
				member.setProfilePictureUrl(null);
			}
			case DIARY -> {
				Diary diary = diaryRepository.findById(id)
					.orElseThrow(() -> new DiaryException(DiaryErrorCode.DIARY_NOT_FOUND));

				path = diary.getImageUrl();
				diary.setImageUrl(null);
			}
			case BOARD -> {
				Attachment attachment = attachmentRepository.findById(id)
					.orElseThrow(() -> new UploadException(UPLOAD_FILE_NOT_FOUND));

				path = attachment.getUrl();
				attachmentRepository.delete(attachment);
			}
			default -> throw new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_TARGET);
		}

		if (path != null && !path.isBlank()) {
			log.info("파일 삭제: path={}", target, path);
			Ut.rm(Path.of(fileDirPath).resolve(path).toString());
		}
	}

	private void applyUploadPathToDomain(FileUploadTarget target, long parentId, Path path, String originalName) {
		switch (target) {
			case PROFILE -> {
				Member member = memberRepository.findById(parentId)
					.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
				member.setProfilePictureUrl(path.toString());
			}
			case DIARY -> {
				Diary diary = diaryRepository.findById(parentId)
					.orElseThrow(() -> new DiaryException(DiaryErrorCode.DIARY_NOT_FOUND));
				diary.setImageUrl(path.toString());
			}
			case BOARD -> {
				Post post = boardRepository.findById(parentId)
					.orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));
				Attachment attachment = Attachment.builder()
					.url(path.toString())
					.originalName(originalName)
					.post(post)
					.build();

				post.getAttachments().add(attachment);
			}
			default -> throw new UploadException(UploadErrorCode.UPLOAD_FILE_INVALID_TARGET);
		}
	}
}
