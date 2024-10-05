package com.example.workhive.controller;

import com.example.workhive.domain.dto.DepartmentDTO;
import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.MessageDTO;
import com.example.workhive.domain.dto.TeamDTO;
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
	private final MessageRepository messageRepository;
	private final DepartmentRepository departmentRepository;
	private final TeamRepository TeamRepository;
	private final MemberDetailRepository memberDetailRepository;
	private final FileRepository fileRepository;

	//${}를 통해 설정파일에서 값을 추출해서 uploadPath에 주입(파일 업로드용 경로)
	@Value("${main.board.uploadPath}")
	String uploadPath;

	// 쪽지 작성 폼을 보여줌
	@GetMapping("MessageForm")
	public String messageform(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		// 로그인된 사용자의 ID 가져오기
		String loggedInUserId = user.getMemberId();
		// 해당 사용자의 멤버 엔티티를 조회
		MemberEntity member = usersRepository.findByMemberId(loggedInUserId);
		// 사용자의 회사 URL을 가져옴
		Long companyId = member.getCompany().getCompanyId();
		System.out.println(uploadPath);
		// 모델에 로그인된 사용자 ID와 회사 URL을 추가하여 뷰에 전달
		model.addAttribute("loggedInUserId", loggedInUserId); // 모델에 추가하여 뷰에 전달
		model.addAttribute("CompanyId", companyId);

		// 쪽지 작성 폼 뷰로 이동
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
			return "main/board/Message";
		} catch (Exception e) {
			// 예외 발생 시 스택 트레이스 출력
			e.printStackTrace();
			// 에러 메시지와 함께 입력된 데이터 모델에 추가

			return "main/board/Message";
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
		return departments.stream()
				.map(dept -> DepartmentDTO.builder()
						.departmentId(dept.getDepartmentId())
						.departmentName(dept.getDepartmentName())
						.build())
				//변환된 DepartmentDTO객체들이 리스트로 모아져 반환됨
				.collect(Collectors.toList());
	}

	// 부서 번호로 하위 부서 목록 조회 (비동기 처리)(요청되는 값 : 부서번호)
	@GetMapping("teams")
	@ResponseBody
	public List<TeamDTO> getTeams(@RequestParam("departmentId") Long departmentId) {
		// 부서번호로 서브 부서 목록을 가져온다
		List<TeamEntity> Teams = messageService.getTeamsByDepartmentId(departmentId);
		System.out.println("Retrieved teams: " + Teams); // 추가된 로그
		// 위에서 받아온 리스트를 스트림으로 변환
		return Teams.stream()
				// 맵 함수를 이용해 스트림의 각 요소를 변환
				.map(subDept -> TeamDTO.builder()
						.teamId(subDept.getTeamId())
						.teamName(subDept.getTeamName())
						.build())
				//변환된 TeamDTO객체들이 리스트로 모아져 반환됨
				.collect(Collectors.toList());
	}

	// 하위부서로 소속된 사원들 조회(요청되는 값 : 하위부서번호)
	// CORS 설정: 특정 출처에서의 요청 허용
	@CrossOrigin(origins = "http://localhost::8080")
	@GetMapping("members")
	@ResponseBody
	public List<MemberDTO> getMembers(@RequestParam("teamId") Long teamId) {
		//서브 부서 번호를 이용해서 멤버리스트 획득
		List<MemberDTO> members = messageService.getMembersBySubDep(teamId);
		//위에서 받아온 리스트 members를 스트림으로 변환
		return members.stream()
				// 맵 함수를 이용해 스트림의 각 요소를 변환
				.map(member -> MemberDTO.builder()
						.memberId(member.getMemberId())
						.memberName(member.getMemberName())
						.build())
				//변환된 MemberDTO객체들이 리스트로 모아져 반환됨
				.collect(Collectors.toList());
	}

	// 받은 보관함에서 쪽지를 읽을 시 생기는 답장기능(요청되는 값 : 메세지번호)
	@GetMapping("reply")
	public String replyMessage(@RequestParam("messageId") Long messageId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
		// 메시지 번호를 기반으로 메시지 조회
		MessageEntity message = messageRepository.findByMessageId(messageId);
		// // 현재 로그인된 사용자 ID를 currentUserId라는 변수에 담기
		String currentUserId = userDetails.getUsername();

		// 발신자 정보 및 부서, 하위부서 정보를 DTO에 담아 전달
		MessageDTO replyMessage = new MessageDTO();
		// 수신자를 발신자로 설정
		replyMessage.setReceiverId(message.getSender().getMemberId()); // 발신자는 자동으로 수신자로 설정
		// 발신자를 수신자로 설정
		replyMessage.setSenderId(currentUserId);
		// 제목에 'Re:' 붙여서 답장으로 표시
		replyMessage.setTitle("Re: " + message.getTitle());
		// 내용에 원본 메세지 내용 담아주기
		replyMessage.setContent("\n\n--- 원본 메시지 ---\n" + message.getContent());
		// 부서 및 하위 부서 정보를 모델에 추가
		MemberDetailEntity senderDetail = memberDetailRepository.findByMember_MemberId(currentUserId);
		DepartmentEntity departmentEntity = departmentRepository.findByDepartmentId(senderDetail.getDepartment().getDepartmentId());
		TeamEntity TeamEntity = TeamRepository.findByTeamId(senderDetail.getTeam().getTeamId());
		model.addAttribute("departmentName", departmentEntity.getDepartmentName());
		model.addAttribute("TeamName", TeamEntity.getTeamName());
		// 답장 작성 페이지로 이동
		model.addAttribute("replyMessage", replyMessage);
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