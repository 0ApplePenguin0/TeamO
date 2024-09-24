package com.example.workhive.repository;

import com.example.workhive.domain.entity.MemberDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberDetailRepository extends JpaRepository<MemberDetailEntity, Long> {
    // subDepartment 대신 team 필드를 사용하여 쿼리 메서드 수정
    List<MemberDetailEntity> findByTeamTeamId(int teamId);
}
