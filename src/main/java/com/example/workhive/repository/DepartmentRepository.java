package com.example.workhive.repository;

import com.example.workhive.domain.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DepartmentRepository
        extends JpaRepository<DepartmentEntity, Integer> {
    List<DepartmentEntity> findByCompany_CompanyUrl(String companyUrl);

    DepartmentEntity findByDepartmentNum(int departmentNum);

}