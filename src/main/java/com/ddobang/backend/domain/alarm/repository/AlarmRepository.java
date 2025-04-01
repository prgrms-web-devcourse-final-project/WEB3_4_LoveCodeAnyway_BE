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

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

	//  사용자의 알림 목록 페이징으로 조회
	Page<Alarm> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

	// 특정알림 조회
	Optional<Alarm> findByIdAndReceiverId(Long id, Long receiverId);

	// 읽지 않은 알림 개수 조회
	long countByReceiverIdAndReadStatus(Long receiverId, boolean readStatus);

	// 전부 읽음 처리
	@Modifying
	@Query("UPDATE Alarm a SET a.readStatus = true WHERE a.receiverId = :receiverId AND a.readStatus = false")
	int markAllAsReadByReceiverId(@Param("receiverId") Long receiverId);

	// TODO?: 오래된 알림 삭제 (예: 90일 이상 지난 알림) - 필요할까?
	@Modifying
	@Query(value =
		"DELETE FROM alarm " +
			"WHERE created_at < :date",
		nativeQuery = true)
	int deleteOldAlarms(@Param("date") LocalDateTime date);

}
