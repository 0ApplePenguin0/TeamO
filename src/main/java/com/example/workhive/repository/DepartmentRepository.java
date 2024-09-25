package com.example.workhive.repository;

import com.example.workhive.domain.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



public interface DepartmentRepository
        extends JpaRepository<DepartmentEntity, Long> {
    List<DepartmentEntity> findByCompany_CompanyId(Long companyId);

    DepartmentEntity findByDepartmentId(Long departmentId);

}
