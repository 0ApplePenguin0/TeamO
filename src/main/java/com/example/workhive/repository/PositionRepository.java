package com.example.workhive.repository;


import com.example.workhive.domain.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
    List<PositionEntity> findByCompany_CompanyId(Long CompanyId);
    PositionEntity findByPositionId(Long PositionId);
}
