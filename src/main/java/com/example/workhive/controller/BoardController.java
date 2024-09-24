package com.example.workhive.controller;

import com.example.workhive.domain.dto.DepartmentDTO;
import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.MessageDTO;
import com.example.workhive.domain.dto.TeamDTO;
import com.example.workhive.domain.entity.DepartmentEntity;
import com.example.workhive.domain.entity.MemberDetailEntity;
import com.example.workhive.domain.entity.MessageEntity;
import com.example.workhive.domain.entity.TeamEntity;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.*;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.MessageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/main/board")
public class BoardController {

	private final MessageService messageService;
	private final MemberRepository usersRepository;


	//${}를 통해 설정파일에서 값을 추출해서 uploadPath에 주입(파일 업로드용 경로)
	@Value("${main.board.uploadPath:tempUpload}")
	String uploadPath;

	// 쪽지함 페이지로 이동
	@GetMapping("Message")
	public String message() {
		// 쪽지함 뷰로 이동
		return "main/board/Message";
	}

	// 쪽지 작성 폼을 보여줌
	@GetMapping("MessageForm")
	public String messageform(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		// 로그인된 사용자의 ID 가져오기
		return "main/board/MessageForm";
	}

	// 쪽지 작성 요청을 처리(요청되는 값 : 업로드)
	@PostMapping("write")
	public String write(@ModelAttribute MessageDTO messageDTO,
						@AuthenticationPrincipal AuthenticatedUser user,
						@RequestParam("upload") MultipartFile upload,
						Model model) {
		String loggedInUserId = user.getMemberId();
		MemberEntity member = usersRepository.findByMemberId(loggedInUserId);

		Long companyId = member.getCompany().getCompanyId();




		try {
			// 쪽지 작성 서비스 호출
			messageService.write(messageDTO, uploadPath, upload, companyId);
			// 쪽지 작성 성공(DB에 저장) 시 홈으로 리다이렉트
			return "redirect:/";
		} catch (Exception e) {
			// 예외 발생 시 스택 트레이스 출력
			e.printStackTrace();
			// 에러 메시지와 함께 입력된 데이터 모델에 추가

			return "main/board/Message";
		}
	}

	// 발신한 쪽지 목록을 조회하여 보여줌
	@GetMapping("SentMessage")
	public String sentlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		// 로그인한 사용자의 발신 쪽지 목록을 조회
		List<MessageDTO> sentMessage = messageService.getsentListAll(user.getMemberId());
		// 모델에 발신 쪽지 목록을 추가하여 뷰에 전달
		model.addAttribute("sentMessageList", sentMessage);
		// 발신 쪽지 뷰로 이동
		return "main/board/SentMessage";
	}

	// 받은 쪽지 목록을 조회하여 보여줌
	@GetMapping("ReceivedMessage")
	public String receivedlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		// 로그인한 사용자의 수신 쪽지 목록을 조회
		List<MessageDTO> receivedMessage = messageService.getreceivedListAll(user.getMemberId());
		// 모델에 수신 쪽지 목록을 추가하여 뷰에 전달
		model.addAttribute("receivedMessageList", receivedMessage);
		// 수신 쪽지 뷰로 이동
		return "main/board/ReceivedMessage";
	}

	// 삭제된 쪽지 목록을 조회하여 보여줌
	@GetMapping("DeletedMessage")
	public String deltedlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		// 로그인한 사용자의 삭제된 쪽지 목록을 조회
		List<MessageDTO> deletedMessageList = messageService.getdeletedListAll(user.getMemberId());
		// 모델에 삭제된 쪽지 목록을 추가하여 뷰에 전달
		model.addAttribute("deletedMessageList", deletedMessageList);
		// 삭제된 쪽지 뷰로 이동
		return "main/board/DeletedMessage";
	}

	// 수신 쪽지 내용 읽기(요청되는 값 : 메세지번호)
	@GetMapping("readReceived")
	public String readreceived(Model model, @RequestParam("messageNum") int messageNum) {
	
			return "redirect:/";


	}

	// 발신 쪽지 내용 읽기(요청되는 값 : 메세지번호)
	@GetMapping("readSent")
	public String readsent(Model model, @RequestParam("messageNum") int messageNum) {
		
			return "redirect:/";

	}


	// 쪽지 읽음 상태 업데이트 (비동기 처리)(요청되는 값 : 메세지번호)
	@GetMapping("updateReadStatus")
	@ResponseBody
	public String updateReadStatus(@RequestParam("messageId") Long messageId) {
		try {
			// 쪽지 읽음 상태 업데이트
			messageService.updateReadStatus(messageId);
			// 성공 응답
			return "success";
		} catch (Exception e) {
			// 예외 발생 시 스택 트레이스 출력
			e.printStackTrace();
			// 실패 응답
			return "error";
		}
	}

	// 쪽지 삭제보관함으로 이동(비동기 처리)(요청되는 값 : 메세지번호)
	@GetMapping("updateDeleteStatus")
	@ResponseBody
	public String updateDeleteStatus(@RequestParam("messageId") Long messageId) {
		try {
			// 쪽지 삭제 상태 업데이트
			messageService.updateDeleteStatus(messageId);
			// 성공 응답
			return "success";
		} catch (Exception e) {
			// 예외 발생 시 스택 트레이스 출력
			e.printStackTrace();
			// 실패 응답
			return "error";
		}
	}

	// 삭제된 쪽지 복원 (비동기 처리)(요청되는 값 : 메세지번호)
	@GetMapping("restoreMessage")
	@ResponseBody
	public String restoreMessage(@RequestParam("messageId") Long messageId) {
		try {
			// 쪽지 삭제 상태 업데이트(삭제 false로)
			messageService.restoreMessage(messageId);
			// 성공 응답
			return "success";
		} catch (Exception e) {
			// 예외 발생 시 스택 트레이스 출력
			e.printStackTrace();
			// 실패 응답
			return "error";
		}
	}

	// 쪽지 완전 삭제 처리(요청되는 값 : 메세지번호)
	@GetMapping("delete")
	public String delete(@RequestParam("messageId") Long messageId) {
		try {
			// 쪽지 삭제 서비스 호출
			messageService.delete(messageId);
			// 성공 시 쪽지함 페이지로 이동
			return "main/board/Message";
		} catch (Exception e) {
			// 예외 발생 시 스택 트레이스 출력
			e.printStackTrace();
			// 실패 시 쪽지 작성 폼으로 이동
			return "main/board/MessageForm";
		}
	}

	// 쪽지 읽을 때 첨부 파일 다운로드 처리(요청되는 값 : 메세지번호)
	@GetMapping("download")
	public void download(
			@RequestParam("messageId") Integer messageId
			, HttpServletResponse response) {

		// 파일 다운로드 서비스 호출
		messageService.download(messageId, response, uploadPath);
	}

	// 부서 선택 기능용 url들
	// 회사 URL로 부서 목록 조회 (비동기 처리)(요청되는 값 : 회사URL)
	@GetMapping("departments")
	@ResponseBody
	public List<DepartmentDTO> getDepartmentsByCompanyId(@RequestParam("companyId") Long companyId) {
		// 회사 URL로 부서 목록을 조회
		List<DepartmentEntity> departments = messageService.getDepartmentsByCompanyId(companyId);
		// 위에서 받아온 리스트를 스트림으로 변환
		return null;
	}

	// 하위부서로 소속된 사원들 조회(요청되는 값 : 하위부서번호)
	// CORS 설정: 특정 출처에서의 요청 허용
	@CrossOrigin(origins = "http://localhost::8080")
	@GetMapping("members")
	@ResponseBody
	public List<MemberDTO> getMembers(@RequestParam("teamId") Long teamId) {
		//서브 부서 번호를 이용해서 멤버리스트 획득
		return null;
	}

		// 받은 보관함에서 쪽지를 읽을 시 생기는 답장기능(요청되는 값 : 메세지번호)
		@GetMapping("reply")
		public String replyMessage(@RequestParam("messageNum") int messageNum, Model model, @AuthenticationPrincipal UserDetails userDetails) {

			return "main/board/replyMessageForm";  // 답장 작성 페이지로 이동
		}

	//작성한 답장 메세지를 전송하는 기능(요청되는 값 : 업로드)
	@PostMapping("sendReply")
	public String sendreply(@ModelAttribute MessageDTO messageDTO, @RequestParam("upload") MultipartFile upload, @AuthenticationPrincipal AuthenticatedUser user) {

		// 로그인된 사용자의 ID 가져오기
		String loggedInUserId = user.getMemberId();
		// 해당 사용자의 멤버 엔티티를 조회
		MemberEntity member = usersRepository.findByMemberId(loggedInUserId);
		// 사용자의 회사 URL을 가져옴
		Long companyId = member.getCompany().getCompanyId();


		try {
			// 메시지 서비스에서 답장 메시지 처리
			messageService.sendReply(messageDTO, upload, uploadPath, companyId);
			// 성공 시 메인 페이지로 리다이렉트
			return "redirect:/";

		} catch (Exception e) {
			// 예외 발생 시 스택 트레이스 출력
			e.printStackTrace();
			// 실패 시 메인 페이지로 리다이렉트
			return "redirect:/";

		}
	}
}
