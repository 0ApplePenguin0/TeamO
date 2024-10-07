package com.example.workhive.repository;

import com.example.workhive.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    // 회원 ID로 회원 정보 조회
    MemberEntity findByMemberId(String memberId);

    // 이메일 중복 확인
    boolean existsByEmail(String searchEmail);

    // 회사 ID로 멤버 조회
    List<MemberEntity> findByCompany_CompanyId(Long companyId);

    // 회사 ID와 이름으로 직원 검색
    List<MemberEntity> findByCompany_CompanyIdAndMemberNameContaining(Long companyId, String name);

    // 부서 ID로 멤버 조회 추가
    List<MemberEntity> findByMemberDetail_Department_DepartmentId(Long departmentId);

    List<MemberEntity> findByMemberDetail_Team_TeamId(Long teamId);

    List<MemberEntity> findByCompanyCompanyId(Long companyId);
}
