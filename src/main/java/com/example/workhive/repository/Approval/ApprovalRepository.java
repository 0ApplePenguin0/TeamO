package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.ApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<ApprovalEntity, Long> {
    List<ApprovalEntity> findByRequesterMemberId(String memberId);

    List<ApprovalEntity> findByCompany_CompanyId(Long companyId);
}