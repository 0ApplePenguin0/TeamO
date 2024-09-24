package com.example.workhive.repository;

import com.example.workhive.domain.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    // CompanyEntity의 companyId를 기준으로 부서를 찾는 메서드로 변경
    List<DepartmentEntity> findByCompany_CompanyId(Long companyId);
}
