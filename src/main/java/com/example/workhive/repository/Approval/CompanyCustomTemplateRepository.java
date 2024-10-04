package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.CompanyCustomTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyCustomTemplateRepository extends JpaRepository<CompanyCustomTemplateEntity, Long> {
    Optional<CompanyCustomTemplateEntity> findByCompanyCompanyIdAndFormTemplateTemplateId(Long companyId, Long templateId);
}
