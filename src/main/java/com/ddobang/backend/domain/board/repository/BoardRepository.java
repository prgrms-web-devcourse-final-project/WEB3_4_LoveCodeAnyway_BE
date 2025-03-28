package com.ddobang.backend.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.board.entity.Post;

@Repository
public interface BoardRepository extends JpaRepository<Post, Long> {
}
