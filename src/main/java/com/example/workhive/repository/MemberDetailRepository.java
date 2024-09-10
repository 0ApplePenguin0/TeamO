package com.example.workhive.repository;

import com.example.workhive.domain.entity.MemberDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MemberDetailRepository extends JpaRepository<MemberDetailEntity, Integer> {
	List<MemberDetailEntity> findBySubDepartment_SubdepNum(int subdepNum);
}
