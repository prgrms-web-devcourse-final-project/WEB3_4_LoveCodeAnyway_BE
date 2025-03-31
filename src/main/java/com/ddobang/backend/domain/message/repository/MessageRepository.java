package com.ddobang.backend.domain.message.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.message.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	// 수/발신자 메시지 조회
	List<Message> findAllByReceiverId(Long receiverId);

	List<Message> findAllBySenderId(Long senderId);

	// 읽지않은 수신 메세지 조회
	List<Message> findAllByReceiverIdAndIsReadFalse(Long receiverId);

	// 수/발신자 메시지 페이징처리(최신순서로)
	Page<Message> findAllByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

	Page<Message> findAllBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);
}
