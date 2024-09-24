package com.example.workhive.repository;

import com.example.workhive.domain.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
	List<TeamEntity> findByDepartmentDepartmentId(Long DepartmentId);

	TeamEntity findByTeamId(Long TeamId);
}
