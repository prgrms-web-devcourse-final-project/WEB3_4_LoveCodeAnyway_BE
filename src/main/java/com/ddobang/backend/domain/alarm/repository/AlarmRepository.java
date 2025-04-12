package com.ddobang.backend.domain.alarm.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.alarm.entity.Alarm;
import com.ddobang.backend.domain.member.entity.Member;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

	// 사용자의 알림 목록 페이징으로 조회 (Member 기반)
	Page<Alarm> findByReceiverOrderByCreatedAtDesc(Member receiver, Pageable pageable);
	
	// 기존 코드와의 호환성을 위한 메서드 (ID 기반)
	@Query("SELECT a FROM Alarm a WHERE a.receiver.id = :receiverId ORDER BY a.createdAt DESC")
	Page<Alarm> findByReceiverIdOrderByCreatedAtDesc(@Param("receiverId") Long receiverId, Pageable pageable);

	// 특정알림 조회
	Optional<Alarm> findByIdAndReceiver(Long id, Member receiver);

	// 읽지 않은 알림 개수 조회
	long countByReceiverAndReadStatus(Member receiver, boolean readStatus);

	// 전부 읽음 처리
	@Modifying
	@Query("UPDATE Alarm a SET a.readStatus = true WHERE a.receiver = :receiver AND a.readStatus = false")
	int markAllAsReadByReceiver(@Param("receiver") Member receiver);

	// TODO: 오래된 알림 삭제 (예: 90일 이상 지난 알림) - 필요할까?
	@Modifying
	@Query(value =
		"DELETE FROM alarm " +
			"WHERE created_at < :date",
		nativeQuery = true)
	int deleteOldAlarms(@Param("date") LocalDateTime date);

}