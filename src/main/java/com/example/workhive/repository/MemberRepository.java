package com.example.workhive.repository;

import com.example.workhive.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MemberRepository
        extends JpaRepository<MemberEntity, String> {
        MemberEntity findByMemberId(String memberId);
        MemberEntity findByEmail(String email);
        boolean existsByEmail(String searchEmail);
        List<MemberEntity> findByCompany_CompanyId(Long companyId);

        // 회사 ID와 이름으로 직원 검색
        List<MemberEntity> findByCompany_CompanyIdAndMemberNameContaining(Long companyId, String name);

}