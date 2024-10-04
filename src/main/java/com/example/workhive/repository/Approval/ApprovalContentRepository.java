package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.ApprovalContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalContentRepository extends JpaRepository<ApprovalContentEntity, Long> {
}
