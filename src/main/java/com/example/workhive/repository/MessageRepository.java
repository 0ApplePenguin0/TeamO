package com.example.workhive.repository;

import com.example.workhive.domain.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
	 MessageEntity findByMessageNum(Integer messageNum);
	List<MessageEntity> findByDeleteDateBefore(LocalDateTime dateTime);

}
