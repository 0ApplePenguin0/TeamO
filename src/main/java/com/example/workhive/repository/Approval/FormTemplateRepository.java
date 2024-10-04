package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.FormTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormTemplateRepository extends JpaRepository<FormTemplateEntity, Long> {
    List<FormTemplateEntity> findByCompanyCompanyIdAndIsActiveTrue(Long companyId);
}