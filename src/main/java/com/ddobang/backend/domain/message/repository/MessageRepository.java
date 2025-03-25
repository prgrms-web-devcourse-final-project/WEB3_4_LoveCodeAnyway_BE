package com.ddobang.backend.domain.message.repository;

import com.ddobang.backend.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
