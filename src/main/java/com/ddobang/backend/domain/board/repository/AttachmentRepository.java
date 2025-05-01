package com.ddobang.backend.domain.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.board.entity.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

	@Query("SELECT a.url FROM Attachment a WHERE a.post.id = :postId")
	List<String> findUrlsByPostId(Long postId);
}
