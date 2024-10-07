package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemoDTO;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.MemoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.workhive.repository.DepartmentRepository;
import com.example.workhive.repository.MemberDetailRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.MessageRepository;
//9/25 수정 import com.example.workhive.repository.SubDepartmentRepository;
import com.example.workhive.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/memo")
public class MemoController {
	
	private final MemberRepository memberRepository;
	private final MemoService memoService;


	/**
	*
	* memoList
	* :메모장 리스트
	* (메모를 dto에서 불러와서 list화 시켜 출력한다)
	* */


	@Value("${memo.pageSize}")
	int pageSize;

	@Value("${memo.linkSize}")
	int linkSize;


	@GetMapping("/list")
	public String list(Model model,
			@AuthenticationPrincipal AuthenticatedUser user
			,@RequestParam(name="page", defaultValue="1") int page) {
	    // 서비스에서 전체 글 목록을 전달받음 + 로그인한 사용자에 대한 메모만 조회
		Page<MemoDTO> memoPage = memoService.getList(user.getUsername(), page, pageSize);

	    // 글 목록을 모델에 저장
	    model.addAttribute("memoPage", memoPage);
		model.addAttribute("page", page);
		// HTML로 포워딩하여 출력
	    return "memo/memo";
	}


	/**
	* addMemo
	* 메모장을 작성
	* */

	@GetMapping("/add")
	public String memoAdd() {
		return "memo/addMemo";
	}

	/**
	 * 작성한 메모 저장
	 * @param memoDTO
	 * @param user
	 * @return
	 */
	@ResponseBody
	@PostMapping("addMemo")
	public String write(@RequestBody MemoDTO memoDTO, @AuthenticationPrincipal AuthenticatedUser user) {
		System.out.println("Content: " + memoDTO.getContent());  // Content 값 확인
		memoService.add(memoDTO, user);
		return "redirect:list"; // 성공적으로 저장 후 다른 페이지로 리디렉션
	}


	
	/**
	 * 게시글 상세보기
	 * @param model     모델
	 * @param memoId  조회할 글 번호
	 * @return 게시글 상세보기 HTML 경로
	 */
	@GetMapping("read")
	public String read(@RequestParam("memoId") Long memoId, Model model) {
	    
	    try {
	        MemoDTO dto = memoService.getMemo(memoId);
	        model.addAttribute("memo", dto);
	        return "memo/read";
	    }
	    catch (Exception e) { //글이 없을시 오류를 만들어서 throw함
	        e.printStackTrace();
	        return "redirect:list";
	    }
	    //해당 오류 발생시 e.printStackTrace();로 오류문 콘솔 출력시킴 (log로 예외객체 출력도 가능함)
	}



	/**
	 * 본인 게시글 삭제
	 * @param memoId   삭제할 글번호
	 * @param user     로그인 정보
	 * @return         게시판 글목록 보기 경로
	 */
	@DeleteMapping("/deleteMemo/{memoId}")
	@ResponseBody
	public ResponseEntity<?> deleteMemo(@PathVariable("memoId") Long memoId,
										@AuthenticationPrincipal AuthenticatedUser user) {
		try {
			memoService.delete(memoId, user.getUsername());
			return ResponseEntity.ok().build();  // 삭제 성공 시 상태 코드 200 반환
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // 오류 시 500 에러 반환
		}
	}



	/**
	 * 게시글 수정 폼으로 이동
	 * @param memoId     수정할 글번호
	 * @param user         로그인한 사용자 정보
	 * @return             수정폼 HTML
	 */
	@GetMapping("update")
	public String update(
	        Model model,
	        @RequestParam("memoId") Long memoId,
	        @AuthenticationPrincipal AuthenticatedUser user) {

	    try {
	        MemoDTO memoDTO = memoService.getMemo(memoId);
	        
	        if (!user.getUsername().equals(memoDTO.getMemberId())) {
	            throw new RuntimeException("수정 권한이 없습니다.");
	        }
	        model.addAttribute("memo", memoDTO);
	        return "memo/update";
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return "redirect:list";
	    }
	}

	/**
	* 게시글 수정 처리
	* @param memoDTO      수정할 글 정보
	* @param user         로그인한 사용자 정보
	* @return             수정폼 HTML
	*/
	@PostMapping("update")
	public String update(@RequestBody MemoDTO memoDTO,
						 @AuthenticationPrincipal AuthenticatedUser user) {
		try {
			System.out.println("메모 ID: " + memoDTO.getMemoId());  // memoId가 제대로 들어오는지 확인
			System.out.println("메모 내용: " + memoDTO.getContent());  // 내용이 제대로 들어오는지 확인

			memoService.update(memoDTO, user.getUsername());
			return "redirect:read?memoId=" + memoDTO.getMemoId();  // 수정된 메모 페이지로 리디렉션
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:list";  // 에러 시 리스트 페이지로 리디렉션
		}
	}

	@DeleteMapping("/deleteMemos")
	@ResponseBody
	public ResponseEntity<String> deleteMemos(@RequestBody List<Long> memoIds,
											  @AuthenticationPrincipal AuthenticatedUser user) {
		try {
			memoService.deleteMemos(memoIds, user.getUsername());
			return ResponseEntity.ok("Memos deleted successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting memos.");
		}
	}





}
