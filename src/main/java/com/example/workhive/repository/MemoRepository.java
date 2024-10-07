package com.example.workhive.repository;


import com.example.workhive.domain.entity.MemoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.workhive.domain.entity.MemberEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoRepository  extends JpaRepository<MemoEntity, Long> {

    //memoId를 기준으로 검색
    List<MemoEntity> findByMemoIdContaining(String s, Sort sort);

    //전달된 문자열을 제목에서 검색한 후 지정한 한페이지 분량 리턴
    Page<MemoEntity> findByMemoIdContaining(String s, Pageable p);

    // 본문을 기준으로 한 페이지 분량의 결과 리턴
    Page<MemoEntity> findByContentContaining(String s, Pageable p);  // memoContent -> content로 수정

    // 사용자별 메모를 페이징 처리하여 조회하는 메서드
    Page<MemoEntity> findByMember(MemberEntity member, Pageable pageable);


}
