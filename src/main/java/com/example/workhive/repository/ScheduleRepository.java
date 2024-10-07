package com.example.workhive.repository;

import com.example.workhive.domain.entity.schedule.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findByMember_MemberId(String memberId);  // MemberEntity의 memberId로 조회

    // category_id가 2이고 category_num이 companyId와 일치하는 스케쥴 조회
    @Query("SELECT s FROM ScheduleEntity s WHERE s.category.categoryId = 2 AND s.categoryNum = :companyId")
    List<ScheduleEntity> findByCategoryIdAndCompanyId(@Param("companyId") Long companyId);

    // category_id가 3이고 category_num이 departmentId와 일치하는 스케쥴 조회
    @Query("SELECT s FROM ScheduleEntity s WHERE s.category.categoryId = 3 AND s.categoryNum = :departmentId")
    List<ScheduleEntity> findByCategoryIdAndDepartmentId(@Param("departmentId") Long departmentId);

    //category_id가 4이고 category_num이 teamId와 일치하는 스케쥴 조회
    @Query("SELECT s FROM ScheduleEntity s WHERE s.category.categoryId = 4 AND s.categoryNum = :teamId")
    List<ScheduleEntity> findByCategoryIdAndTeamId(@Param("teamId") Long teamId);

    // 일정의 소유 여부 확인 (scheduleId와 memberId로)
    boolean existsByScheduleIdAndMember_MemberId(Long scheduleId, String memberId);

    // 오늘의 일정을 날짜 기준으로 조회 (엔티티 전체 조회)
    @Query("SELECT s FROM ScheduleEntity s " +
            "WHERE FUNCTION('DATE', s.startDate) = :todayDate OR FUNCTION('DATE', s.endDate) = :todayDate " +
            "OR (:todayDate BETWEEN FUNCTION('DATE', s.startDate) AND FUNCTION('DATE', s.endDate))")
    List<ScheduleEntity> findSchedulesByDate(@Param("todayDate") LocalDate todayDate);

}
