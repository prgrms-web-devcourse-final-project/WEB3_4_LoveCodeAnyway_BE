package com.ddobang.backend.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.board.dto.request.AdminPostSearchCondition;
import com.ddobang.backend.domain.board.dto.response.PostSummaryResponse;
import com.ddobang.backend.domain.board.types.PostType;

public interface PostRepositoryCustom {

	Page<PostSummaryResponse> findMyPosts(Long memberId, PostType type, String keyword, Pageable pageable);

	Page<PostSummaryResponse> findPostsForAdmin(Pageable pageable, AdminPostSearchCondition condition);
}
