package com.example.workhive.repository;

import com.example.workhive.domain.entity.MessageEntity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
	 MessageEntity findByMessageId(Long messageId);
	List<MessageEntity> findByDeleteDateBefore(LocalDateTime dateTime);

}
