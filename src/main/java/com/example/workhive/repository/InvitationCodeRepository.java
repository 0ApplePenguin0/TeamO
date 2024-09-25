package com.example.workhive.repository;


import com.example.workhive.domain.entity.InvitationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationCodeRepository extends JpaRepository<InvitationCodeEntity, Long> {
    InvitationCodeEntity findByCode(String code);
}
