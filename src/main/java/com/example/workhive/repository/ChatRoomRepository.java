package com.example.workhive.repository;

import com.example.workhive.domain.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Integer> {
	
	  @Query("SELECT c FROM ChatRoomEntity c LEFT JOIN c.invitedUsers u WHERE c.createdById = :userId OR u.memberId = :userId")
	    List<ChatRoomEntity> findByCreatedByIdOrInvitedUsers(@Param("userId") String userId);
	  
	  // 채팅방 이름으로 채팅방 조회
	    Optional<ChatRoomEntity> findByChatRoomName(String chatRoomName);
}
