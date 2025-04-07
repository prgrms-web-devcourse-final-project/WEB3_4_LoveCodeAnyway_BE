package com.ddobang.backend.domain.message.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.message.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	// 수/발신자 메시지 조회
	List<Message> findAllByReceiverId(Long receiverId);

	List<Message> findAllBySenderId(Long senderId);

	// 읽지않은 수신 메세지 조회
	List<Message> findAllByReceiverIdAndIsReadFalse(Long receiverId);

	// 무한 스크롤을 위한 커서 기반 조회 메서드 (수신자 기준)
	// lastMessageId가 null인 경우 첫 페이지 조회
	@Query("SELECT m FROM Message m WHERE m.receiver.id = :receiverId AND "
		+ "(:lastMessageId IS NULL OR m.id < :lastMessageId) "
		+ "ORDER BY m.id DESC")
	List<Message> findByReceiverIdAndIdLessThanOrderByIdDesc(
		@Param("receiverId") Long receiverId,
		@Param("lastMessageId") Long lastMessageId,
		Pageable pageable);

	// 무한 스크롤을 위한 커서 기반 조회 메서드 (발신자 기준)
	// lastMessageId가 null인 경우 첫 페이지 조회
	@Query("SELECT m FROM Message m WHERE m.sender.id = :senderId AND "
		+ "(:lastMessageId IS NULL OR m.id < :lastMessageId) "
		+ "ORDER BY m.id DESC")
	List<Message> findBySenderIdAndIdLessThanOrderByIdDesc(
		@Param("senderId") Long senderId,
		@Param("lastMessageId") Long lastMessageId,
		Pageable pageable);

}