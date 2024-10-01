package com.example.workhive.repository.schedule;

import com.example.workhive.domain.entity.schedule.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {
    List<ScheduleEntity> findByMember_MemberId(String memberId);  // MemberEntity의 memberId로 조회

}
