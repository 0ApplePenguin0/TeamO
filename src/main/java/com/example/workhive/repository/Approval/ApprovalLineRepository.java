package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.ApprovalLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalLineRepository extends JpaRepository<ApprovalLineEntity, Long> {
    List<ApprovalLineEntity> findByApprovalApprovalId(Long approvalId);

    List<ApprovalLineEntity> findByMember_MemberIdAndStatus(String memberId, String pending);
}