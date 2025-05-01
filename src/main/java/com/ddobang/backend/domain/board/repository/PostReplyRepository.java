package com.ddobang.backend.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.board.entity.PostReply;

@Repository
public interface PostReplyRepository extends JpaRepository<PostReply, Long> {
}
