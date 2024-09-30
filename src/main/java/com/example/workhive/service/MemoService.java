package com.example.workhive.service;


import com.example.workhive.domain.dto.MemoDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.MemoEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.MemoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemoService {


    private final MemoRepository memoRepository;
    private final MemberRepository memberRepository;


    /**
     * 게시판 글 저장
     * @param memoDTO 저장할 글 정보
     */
    public void add(MemoDTO memoDTO) {
        MemberEntity memberEntity = memberRepository.findById(memoDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("아이디가 없습니다."));

        MemoEntity entity = new MemoEntity();

        entity.setMember(memberEntity);
        entity.setMemoContent(memoDTO.getMemoContent());

        log.debug("저장되는 엔티티 : {}", entity);

        memoRepository.save(entity);
    }



    /*
     * 검색 결과 글목록을 지정한 한페이지 분량의 Page객체로 리턴
     * 
     * @Param page 			현재 페이지
     * @Param pageSize 		페이지당 글 수
     * @return 				게시글 목록 정보
     * */
    
    public Page<MemoDTO> getList(int page, int pageSize) {
    	
        // 조회조건을 담은 Pageable 객체 생성
    	 Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "memoId");
    	 
    	 // repository의 메소드로 pageable 조회. Page 리턴받음
    	 Page<MemoEntity> entityPage;
    	 
    	 //기본적으로 모든 페이지를 조회
    	 entityPage = memoRepository.findAll(pageable);

        log.debug("조회된 결과 엔티티페이지: {}", entityPage.getContent());

        // entityPage 객체 내의 엔티티들을 DTO 객체로 변환하여 새로운 Page 객체 생성
        Page<MemoDTO> dtoPage = entityPage.map(this::convertToDTO);

        return dtoPage; // 변환된 DTO 페이지 반환
    }

    
    /**
     * DB에서 조회한 게시글 정보인 MemoEntity 객체를 MemoDTO 객체로 변환
     * @param entity    게시글 정보 Entity 객체
     * @return          게시글 정보 DTO 개체
     */
    private MemoDTO convertToDTO(MemoEntity entity) {
        MemoDTO dto = new MemoDTO();
        dto.setMemoId(entity.getMemoId()); // 메모 식별 번호
        dto.setMemberId(entity.getMember() != null ? entity.getMember().getMemberId() : null); // null 체크 추가
        dto.setMemoContent(entity.getMemoContent());
        dto.setCreatedAt(entity.getCreatedAt());
        
        log.debug("dto : {}", dto);
        
        return dto;
        
    }


    /**
     * ReplyEntity객체를 ReplyDTO 객체로 변환
     * @param memoId    리플 정보 Entity 객체
     * @return  dto        리플 정보 DTO 객체
     */

	public MemoDTO getMemo(Long memoId) {
		MemoEntity entity = memoRepository.findById(memoId)
				.orElseThrow(()-> new EntityNotFoundException("글이 없습니다."));
		
	MemoDTO dto = convertToDTO(entity);
	
	log.debug("dto : {}",dto);
	return dto;
	}


	 /**
    * 게시글 삭제
    * @param memoId  삭제할 글번호
    * @param username  로그인한 아이디
    */

	public void delete(Long memoId, String username) {
		 MemoEntity memoEntity = memoRepository.findById(memoId)
	                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

	        if (!memoEntity.getMember().getMemberId().equals(username)) {
	            throw new RuntimeException("삭제 권한이 없습니다.");
	        }
	        memoRepository.delete(memoEntity);
	    }


	 /**
	 * 게시글 수정
	 * @param memoDTO      수정할 글정보
	 * @param username      로그인한 아이디
	 */
	public void update(MemoDTO memoDTO, String username) 
			 throws Exception {
	    MemoEntity memoEntity = memoRepository.findById(memoDTO.getMemoId())
	            .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

	    if (!memoEntity.getMember().getMemberId().equals(username)) {
	        throw new RuntimeException("수정 권한이 없습니다.");
	    }

	    //전달된 정보 수정
	    memoEntity.setMemoContent(memoDTO.getMemoContent());
	    
	    memoRepository.save(memoEntity);
		
	}
    

}



