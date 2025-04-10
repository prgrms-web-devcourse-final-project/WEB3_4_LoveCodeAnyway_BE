package com.ddobang.backend.domain.message.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
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

	// 수/발신자 메시지 페이징처리(최신순서로)
	Page<Message> findAllByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

	Page<Message> findAllBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);

	// 첫 페이지용 (커서가 없을 때)
	@Query("SELECT m FROM Message m WHERE m.receiver.id = :receiverId ORDER BY m.createdAt DESC LIMIT :size")
	List<Message> findFirstReceivedMessages(
		@Param("receiverId") Long receiverId,
		@Param("size") int size);

	// 첫 페이지용 (커서가 없을 때)
	@Query("SELECT m FROM Message m WHERE m.sender.id = :senderId ORDER BY m.createdAt DESC LIMIT :size")
	List<Message> findFirstSentMessages(
		@Param("senderId") Long senderId,
		@Param("size") int size);

	// 커서 기반 무한 스크롤용 메서드 - 보낸 메시지
	@Query("SELECT m FROM Message m WHERE m.sender.id = :senderId AND m.createdAt < :cursorTime ORDER BY m.createdAt DESC LIMIT :size")
	List<Message> findSentMessagesBeforeCursor(
		@Param("senderId") Long senderId,
		@Param("cursorTime") LocalDateTime cursorTime,
		@Param("size") int size);

}
