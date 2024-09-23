package com.example.workhive.repository;

import com.example.workhive.domain.entity.SubDepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SubDepartmentRepository extends JpaRepository<SubDepartmentEntity, Integer> {
	List<SubDepartmentEntity> findByDepartmentDepartmentId(int DepartmentId);

	SubDepartmentEntity findBySubdepNum(int subdepNum);
}
