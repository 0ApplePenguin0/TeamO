package com.example.workhive.repository;

import com.example.workhive.domain.entity.MemberDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MemberDetailRepository extends JpaRepository<MemberDetailEntity, Long> {
		List<MemberDetailEntity> findByTeam_TeamId(Long teamId);
		MemberDetailEntity findByMember_MemberId(String memberId);
	}
