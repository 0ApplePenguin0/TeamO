package com.example.workhive.repository;


import com.example.workhive.domain.entity.MemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository  extends JpaRepository<MemoEntity, Integer> {

}
