package com.example.workhive.repository;

import com.example.workhive.domain.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    // companyId를 기준으로 Optional로 회사 조회
    Optional<CompanyEntity> findByCompanyId(Long companyId);
    CompanyEntity findByCompanyId(Long CompanyId);

    boolean existsByCompanyId(Long CompanyId);

}
