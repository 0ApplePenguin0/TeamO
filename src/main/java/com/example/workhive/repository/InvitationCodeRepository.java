package com.example.workhive.repository;


import com.example.workhive.domain.entity.InvitationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationCodeRepository extends JpaRepository<InvitationCodeEntity, Long> {
    InvitationCodeEntity findByCode(String code);

    Optional<InvitationCodeEntity> findByCodeAndIsActiveTrue(String code);

}
