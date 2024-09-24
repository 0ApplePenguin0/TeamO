package com.example.workhive.repository;

import com.example.workhive.domain.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository
public interface DepartmentRepository
        extends JpaRepository<DepartmentEntity, Long> {
    List<DepartmentEntity> findByCompany_CompanyId(Long companyId);

    DepartmentEntity findByDepartmentId(Long departmentId);

}
