package com.example.workhive.repository;

import com.example.workhive.domain.entity.ChatRoomKindEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 채팅방 종류 Repository
 */
@Repository
public interface ChatRoomKindRepository extends JpaRepository<ChatRoomKindEntity, Long> {

    Optional<ChatRoomKindEntity> findByChatroomKindId(Long chatroomKindId);

    // kind 값을 기준으로 ChatRoomKindEntity를 검색하는 메서드 추가
    Optional<ChatRoomKindEntity> findByKind(String kind);
}
