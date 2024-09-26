package com.example.workhive.repository.schedule;

import com.example.workhive.domain.entity.schedule.CategoryEntity;
import com.example.workhive.domain.entity.schedule.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
}
