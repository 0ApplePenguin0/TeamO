package com.example.workhive.repository;

import com.example.workhive.domain.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    // companyId를 기준으로 Optional로 회사 조회
    CompanyEntity findByCompanyId(Long CompanyId);
    boolean existsByCompanyUrl(String CompanyUrl);


}
