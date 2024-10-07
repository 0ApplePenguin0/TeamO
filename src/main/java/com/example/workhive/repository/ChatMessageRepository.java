package com.example.workhive.repository;

import com.example.workhive.domain.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // 특정 채팅방의 메시지를 조회
    List<ChatMessageEntity> findByChatRoom_ChatRoomId(Long chatRoomId);
}
