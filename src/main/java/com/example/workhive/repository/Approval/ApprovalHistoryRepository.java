package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.ApprovalHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistoryEntity, Long> {
}