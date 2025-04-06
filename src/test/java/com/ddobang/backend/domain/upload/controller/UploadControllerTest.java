package com.ddobang.backend.domain.upload.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.board.entity.Post;
import com.ddobang.backend.domain.board.repository.BoardRepository;
import com.ddobang.backend.domain.board.types.PostType;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.upload.service.UploadService;
import com.ddobang.backend.domain.upload.types.FileUploadTarget;
import com.ddobang.backend.global.entity.Attachment;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UploadControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private BoardRepository boardRepository;

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
	}

	@Test
	@DisplayName("파일 업로드, 문의게시판")
	@WithMockUser(roles = "USER")
	void t1_1() throws Exception {
		Member member = memberRepository.findById(1L).orElseThrow();
		Post post = boardRepository.save(
			Post.builder()
				.type(PostType.THEME)
				.member(member)
				.title("테스트")
				.content("내용")
				.build()
		);

		ResultActions resultActions = mvc
			.perform(
				multipart("/api/v1/uploads/" + post.getId())
					.file(mockImage)
					.param("target", "BOARD")
			)
			.andDo(print());

		em.flush();

		resultActions
			.andExpect(handler().handlerType(UploadController.class))
			.andExpect(handler().methodName("upload"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("파일 저장에 성공했습니다."));

		List<Attachment> attachments = post.getAttachments();
		assertThat(attachments).hasSize(1);
		assertThat(attachments.get(0).getOriginalName()).isEqualTo("test-image.png");
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
}
