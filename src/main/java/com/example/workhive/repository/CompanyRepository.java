package com.example.workhive.repository;

import com.example.workhive.domain.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompanyRepository
        extends JpaRepository<CompanyEntity, String> {
    CompanyEntity findByCompanyUrl(String companyUrl);
}