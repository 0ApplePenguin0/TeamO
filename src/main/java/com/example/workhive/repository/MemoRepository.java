package com.example.workhive.repository;


import com.example.workhive.domain.entity.MemoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoRepository  extends JpaRepository<MemoEntity, Long> {


    List<MemoEntity> findByMemoIdContaining(String s, Sort sort);
    //Title:객체 이름 Containing:포함하는 것

    //전달된 문자열을 제목에서 검색한 후 지정한 한페이지 분량 리턴
    Page<MemoEntity> findByMemoIdContaining(String s, Pageable p);

    //전달된 문자열을 본문에서 검색한 후 지정한 한페이지 분량 리턴
    Page<MemoEntity> findByContentContaining(String s, Pageable p);  // memoContent -> content로 수정

    //전달된 문자열을 작성자 아이디에서 검색후 지정한 한페이지 분량 리턴
    Page<MemoEntity> findByMember_MemberIdContaining(String s, Pageable p);

}
