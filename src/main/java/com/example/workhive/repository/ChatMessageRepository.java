package com.example.workhive.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.workhive.domain.entity.ChatMessageEntity;
import com.example.workhive.domain.entity.ChatRoomEntity;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByChatRoom(ChatRoomEntity chatRoom);
}
