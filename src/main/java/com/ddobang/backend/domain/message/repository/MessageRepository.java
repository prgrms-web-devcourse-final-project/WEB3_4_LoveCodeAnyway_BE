package com.ddobang.backend.domain.message.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.message.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	List<Message> findAllByReceiverId(Long receiverId);

}
