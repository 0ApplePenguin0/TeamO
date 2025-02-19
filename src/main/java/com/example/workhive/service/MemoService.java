package com.example.workhive.service;


import com.example.workhive.domain.dto.MemoDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.MemoEntity;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.MemoRepository;
import com.example.workhive.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoService {


    private final MemoRepository memoRepository;
    private final MemberRepository memberRepository;


	/**
	 * 게시판 글 저장
	 * @param memoDTO 저장할 글 정보
	 * @param user 인증된 사용자 정보
	 */
	public void add(MemoDTO memoDTO, AuthenticatedUser user) {
		// 인증된 사용자의 아이디를 이용해 MemberEntity를 찾음
		MemberEntity memberEntity = memberRepository.findById(user.getMemberId())
				.orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// 새로운 MemoEntity 생성
		MemoEntity entity = new MemoEntity();
		entity.setMember(memberEntity);
		entity.setContent(memoDTO.getContent());

		// 저장
		memoRepository.save(entity);
	}


	/**
	 * 검색 결과 글목록을 지정한 한페이지 분량의 Page객체로 리턴
	 * @param page
	 * @param pageSize
	 * @return
	 */
    public Page<MemoDTO> getList(String memberId, int page, int pageSize) {

			// 조회 조건을 담은 Pageable 객체 생성
			Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "memoId");

			// memberId를 기준으로 사용자를 조회
			MemberEntity member = memberRepository.findByMemberId(memberId);
			if (member == null) {
				throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
			}

			// repository의 메소드로 사용자에 따른 pageable 조회. Page 리턴받음
			Page<MemoEntity> entityPage = memoRepository.findByMember(member, pageable);

			// entityPage 객체 내의 엔티티들을 DTO 객체로 변환하여 새로운 Page 객체 생성
			Page<MemoDTO> dtoPage = entityPage.map(this::convertToDTO);

			return dtoPage;
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
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }


    /**
     * MemoEntity객체를 MemoDTO 객체로 변환
     * @param memoId    리플 정보 Entity 객체
     * @return  dto        리플 정보 DTO 객체
     */

	public MemoDTO getMemo(Long memoId) {
		MemoEntity entity = memoRepository.findById(memoId)
				.orElseThrow(()-> new EntityNotFoundException("글이 없습니다."));
		
	MemoDTO dto = convertToDTO(entity);

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
	 public void update(MemoDTO memoDTO, String username) throws Exception {
		 if (memoDTO.getMemoId() == null) {
			 throw new IllegalArgumentException("메모 ID는 null일 수 없습니다.");
		 }

		 MemoEntity memoEntity = memoRepository.findById(memoDTO.getMemoId())
				 .orElseThrow(() -> new EntityNotFoundException("메모를 찾을 수 없습니다."));

		 if (!memoEntity.getMember().getMemberId().equals(username)) {
			 throw new RuntimeException("이 메모를 수정할 권한이 없습니다.");
		 }

		 // 메모 내용 수정
		 memoEntity.setContent(memoDTO.getContent());

		 // 수정된 엔티티 저장
		 memoRepository.save(memoEntity);
	 }

	 //메모 다중 삭제
	public void deleteMemos(List<Long> memoIds, String username) throws Exception {
		for (Long memoId : memoIds) {
			MemoEntity memoEntity = memoRepository.findById(memoId)
					.orElseThrow(() -> new EntityNotFoundException("메모를 찾을 수 없습니다."));

			if (!memoEntity.getMember().getMemberId().equals(username)) {
				throw new RuntimeException("삭제 권한이 없습니다.");
			}
			memoRepository.delete(memoEntity);
		}
	}


}



