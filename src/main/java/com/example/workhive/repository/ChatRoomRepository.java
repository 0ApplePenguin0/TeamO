package com.example.workhive.repository;

import com.example.workhive.domain.entity.ChatRoomEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
	List<ChatRoomEntity> findByChatRoomIdIn(List<Long> chatRoomIds);
}
