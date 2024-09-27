package com.example.workhive.repository;

import com.example.workhive.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MemberRepository
        extends JpaRepository<MemberEntity, String> {
        MemberEntity findByMemberId(String memberId);
        MemberEntity findByEmail(String email);
        boolean existsByEmail(String Email);
}