package com.example.workhive.repository;

import com.example.workhive.domain.entity.ProjectMemberEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, Long> {
    // 필요한 경우 커스텀 쿼리 메서드를 추가할 수 있습니다.
	List<ProjectMemberEntity> findByMember_MemberId(String memberId);
	
	 List<ProjectMemberEntity> findByChatRoom_ChatRoomId(Long chatRoomId);
}
