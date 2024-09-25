package com.example.workhive.repository;

import com.example.workhive.domain.entity.ChatRoomKindEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 채팅방 종류 Repository
 */
@Repository
public interface ChatRoomKindRepository extends JpaRepository<ChatRoomKindEntity, Long> {

    Optional<ChatRoomKindEntity> findByChatroomKindId(Long chatroomKindId);
    
}