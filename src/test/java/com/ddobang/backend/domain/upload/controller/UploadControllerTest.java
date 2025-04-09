package com.ddobang.backend.domain.upload.controller;

import com.ddobang.backend.domain.board.dto.request.PostRequest;
import com.ddobang.backend.domain.board.entity.Attachment;
import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.repository.AttachmentRepository;
import com.ddobang.backend.domain.board.repository.PostRepository;
import com.ddobang.backend.domain.board.types.PostType;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.upload.service.UploadService;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;
import com.ddobang.backend.global.util.Ut;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
public class UploadControllerTest {
	@Autowired
	private MockMvc mvc;

	@Value("${custom.fileUpload.dirPath}")
	private String fileDirPath;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private AttachmentRepository attachmentRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private EntityManager em;

	MockMultipartFile mockImage = new MockMultipartFile(
		"files",                   // 파라미터 이름
		"test-image.png",                 // 파일 이름
		"image/png",                      // MIME 타입
		"dummy-image-content".getBytes()  // 파일 내용
	);

	@AfterEach
	public void deleteFile() throws IOException {
		Ut.rm(fileDirPath);
	}

	@Test
	@DisplayName("파일 업로드, 프로필")
	@WithMockUser(roles = "USER")
	void t1() throws Exception {
		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/1")
					.file(mockImage)
					.param("target", "PROFILE")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("파일 저장에 성공했습니다."));

		Member member = memberRepository.findById(1L).orElseThrow();
		Path path = Path.of(fileDirPath).resolve(member.getProfilePictureUrl());
		File file = path.toFile();

		assertThat(member.getProfilePictureUrl()).contains(
			Path.of(FileUploadTarget.PROFILE.getType())
				.resolve(member.getId().toString())
				.resolve(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")))
				.toString()
		);
		assertThat(file.exists()).isTrue();
	}

	@Test
	@DisplayName("파일 업로드, 문의게시판")
	@WithMockUser(roles = "USER")
	void t1_1() throws Exception {
		Member member = memberRepository.findById(1L).orElseThrow();
		PostRequest request = new PostRequest(PostType.THEME, "테스트", "내용", List.of());
		Post post = postRepository.save(Post.of(request, member));

		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/" + post.getId())
					.file(mockImage)
					.param("target", "BOARD")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("파일 저장에 성공했습니다."));

		List<Attachment> attachments = post.getAttachments();
		Path path = Path.of(fileDirPath).resolve(attachments.get(0).getUrl());
		File file = path.toFile();

		assertThat(file.exists()).isTrue();
		assertThat(attachments).hasSize(1);
		assertThat(attachments.get(0).getFileName()).isEqualTo("test-image.png");
		assertThat(attachments.get(0).getUrl()).contains(
			Path.of(FileUploadTarget.BOARD.getType())
				.resolve(post.getId().toString())
				.resolve(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")))
				.toString()
		);
	}

	@Test
	@DisplayName("파일 업로드, target이 없을 때")
	@WithMockUser(roles = "USER")
	void t1_2() throws Exception {
		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/1")
					.file(mockImage)
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("파일 업로드 대상이 필요합니다."));
	}

	@Test
	@DisplayName("파일 업로드, 잘못된 target")
	@WithMockUser(roles = "USER")
	void t1_3() throws Exception {
		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/1")
					.file(mockImage)
					.param("target", "WRONG_TARGET")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
	}

	@Test
	@DisplayName("파일 업로드, 파일 이름이 없을 때")
	@WithMockUser(roles = "USER")
	void t1_4() throws Exception {
		MockMultipartFile wrongMockImage = new MockMultipartFile(
			"files",                   // 파라미터 이름
			"",                 // 파일 이름
			"image/png",                      // MIME 타입
			"dummy-image-content".getBytes()  // 파일 내용
		);

		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/1")
					.file(wrongMockImage)
					.param("target", "DIARY")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("파일 이름을 확인해주세요."));
	}

	@Test
	@DisplayName("파일 업로드, 허용하지 않는 확장자")
	@WithMockUser(roles = "USER")
	void t1_5() throws Exception {
		MockMultipartFile wrongMockImage = new MockMultipartFile(
			"files",                   // 파라미터 이름
			"test-file.pdf",                 // 파일 이름
			"image/png",                      // MIME 타입
			"dummy-image-content".getBytes()  // 파일 내용
		);

		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/1")
					.file(wrongMockImage)
					.param("target", "DIARY")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("허용하지 않는 확장자입니다."));
	}

	@Test
	@DisplayName("파일 업로드, 잘못 된 확장자")
	@WithMockUser(roles = "USER")
	void t1_6() throws Exception {
		MockMultipartFile wrongMockImage = new MockMultipartFile(
			"files",                   // 파라미터 이름
			"test-file.WRONG_EXT",                 // 파일 이름
			"image/png",                      // MIME 타입
			"dummy-image-content".getBytes()  // 파일 내용
		);

		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/1")
					.file(wrongMockImage)
					.param("target", "DIARY")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("허용하지 않는 확장자입니다."));
	}

	@Test
	@DisplayName("파일 업로드, 확장자가 없는 파일")
	@WithMockUser(roles = "USER")
	void t1_7() throws Exception {
		MockMultipartFile wrongMockImage = new MockMultipartFile(
			"files",                   // 파라미터 이름
			"test-file",                 // 파일 이름
			"image/png",                      // MIME 타입
			"dummy-image-content".getBytes()  // 파일 내용
		);

		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/1")
					.file(wrongMockImage)
					.param("target", "DIARY")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("허용하지 않는 확장자입니다."));
	}

	@Test
	@DisplayName("파일 삭제, 프로필")
	@WithMockUser(roles = "USER")
	void t2() throws Exception {
		mvc
			.perform(
				multipart("/api/v1/uploads/1")
					.file(mockImage)
					.param("target", "PROFILE")
			)
			.andDo(print());

		Member member = memberRepository.findById(1L).orElseThrow();
		Path path = Path.of(fileDirPath).resolve(member.getProfilePictureUrl());
		File file = path.toFile();

		assertThat(file.exists()).isTrue();

		ResultActions resultActions = mvc
			.perform(delete("/api/v1/uploads/1")
				.param("target", "PROFILE")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("파일 삭제에 성공했습니다."));

		assertThat(member.getProfilePictureUrl()).isNull();
		assertThat(file.exists()).isFalse();
	}

	@Test
	@DisplayName("파일 삭제, 문의 게시판")
	@WithMockUser(roles = "USER")
	void t2_1() throws Exception {
		Member member = memberRepository.findById(1L).orElseThrow();
		PostRequest request = new PostRequest(PostType.THEME, "테스트", "내용", List.of());
		Post post = postRepository.save(Post.of(request, member));

		em.flush();

		mvc
			.perform(
				multipart("/api/v1/uploads/" + post.getId())
					.file(mockImage)
					.param("target", "BOARD")
			)
			.andDo(print());

		em.flush();

		Attachment attachment = post.getAttachments().get(0);
		Path path = Path.of(fileDirPath).resolve(attachment.getUrl());
		File file = path.toFile();

		ResultActions resultActions = mvc
			.perform(delete("/api/v1/uploads/" + attachment.getId())
				.param("target", "BOARD")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("파일 삭제에 성공했습니다."));

		assertThat(file.exists()).isFalse();
		assertThat(post.getAttachments()).hasSize(0);
	}

	@Test
	@DisplayName("파일 삭제, 없는 멤버")
	@WithMockUser(roles = "USER")
	void t2_2() throws Exception {
		mvc
			.perform(
				multipart("/api/v1/uploads/99999999")
					.file(mockImage)
					.param("target", "PROFILE")
			)
			.andDo(print());

		ResultActions resultActions = mvc
			.perform(delete("/api/v1/uploads/99999999")
				.param("target", "PROFILE")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("멤버를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("파일 삭제, 없는 파일")
	@WithMockUser(roles = "USER")
	void t2_3() throws Exception {
		ResultActions resultActions = mvc
			.perform(delete("/api/v1/uploads/1")
				.param("target", "DIARY")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("해당 파일을 찾을 수 없습니다."));
	}
}
