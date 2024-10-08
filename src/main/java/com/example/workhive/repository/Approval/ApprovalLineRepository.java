package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.ApprovalLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalLineRepository extends JpaRepository<ApprovalLineEntity, Long> {
    @Query("SELECT al FROM ApprovalLineEntity al WHERE al.member.memberId = :memberId AND al.status = :status")
    List<ApprovalLineEntity> findByMemberIdAndStatus(@Param("memberId") String memberId, @Param("status") String status);
}