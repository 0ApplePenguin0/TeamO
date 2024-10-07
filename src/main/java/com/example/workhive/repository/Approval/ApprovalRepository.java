package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.ApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<ApprovalEntity, Long> {
    List<ApprovalEntity> findByRequesterMemberId(String memberId);

    @Query("SELECT a FROM ApprovalEntity a WHERE a.company.companyId = :companyId")
    List<ApprovalEntity> findApprovalsByCompanyId(@Param("companyId") Long companyId);
}
