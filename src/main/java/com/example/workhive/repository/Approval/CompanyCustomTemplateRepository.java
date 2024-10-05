package com.example.workhive.repository.Approval;

import com.example.workhive.domain.entity.Approval.CompanyCustomTemplateEntity;
import com.example.workhive.domain.entity.Approval.CompanyCustomTemplateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyCustomTemplateRepository extends JpaRepository<CompanyCustomTemplateEntity, CompanyCustomTemplateId> {

    Optional<CompanyCustomTemplateEntity> findByCompanyIdAndTemplateId(Long companyId, Long templateId);

    List<CompanyCustomTemplateEntity> findByCompanyIdAndIsActiveTrue(Long companyId);

    boolean existsByCompanyIdAndTemplateId(Long companyId, Long templateId);
}