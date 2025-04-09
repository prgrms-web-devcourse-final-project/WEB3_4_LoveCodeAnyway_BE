package com.ddobang.backend.domain.board.repository;

import com.ddobang.backend.domain.board.entity.PostReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReplyRepository extends JpaRepository<PostReply, Long> {
}
