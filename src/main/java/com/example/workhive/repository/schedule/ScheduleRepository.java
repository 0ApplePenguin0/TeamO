package com.example.workhive.repository.schedule;

import com.example.workhive.domain.entity.schedule.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findByMember_MemberId(String memberId);  // MemberEntity의 memberId로 조회

    List<ScheduleEntity> findByCompanyId(Long companyId);   // companyId 로 일정조회

    List<ScheduleEntity> findByDepartmentId(Long departmentId); // departmentId 로 일정조회

    List<ScheduleEntity> findByTeamId(Long teamId); // teamId 로 일정조회

}
