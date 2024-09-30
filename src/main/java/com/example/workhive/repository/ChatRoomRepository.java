package com.example.workhive.repository;

import com.example.workhive.domain.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    
    List<ChatRoomEntity> findByChatRoomIdIn(List<Long> chatRoomIds);

    // 단일 채팅방 이름으로 ChatRoomEntity 조회
    Optional<ChatRoomEntity> findByChatRoomName(String chatRoomName);
    
    boolean existsByChatRoomName(String chatRoomName);
}