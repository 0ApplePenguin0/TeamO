package com.example.workhive.repository;


import com.example.workhive.domain.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByAssociatedId(Long associatedId);
}
