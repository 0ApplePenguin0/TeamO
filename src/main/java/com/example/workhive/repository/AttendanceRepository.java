package com.example.workhive.repository;

import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.attendance.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    Optional<AttendanceEntity> findByMemberAndAttendanceDate(MemberEntity member, LocalDate date);
    List<AttendanceEntity> findByMemberAndAttendanceDateBetween(MemberEntity member, LocalDate startDate, LocalDate endDate);
}
