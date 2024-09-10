package com.example.workhive.controller;

import com.example.workhive.domain.dto.DepartmentDTO;
import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.MessageDTO;
import com.example.workhive.domain.dto.SubDepartmentDTO;
import com.example.workhive.domain.entity.DepartmentEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.MessageEntity;
import com.example.workhive.domain.entity.SubDepartmentEntity;
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
	private final SubDepartmentRepository subDepartmentRepository;
	private final MemberDetailRepository memberDetailRepository;

	@Value("${main.board.uploadPath:tempUpload}")
	String uploadPath;

	@GetMapping("Message")
	public String message() {
		return "main/board/Message";
	}

	@GetMapping("MessageForm")
	public String messageform(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		String loggedInUserId = user.getMemberId(); // 로그인된 사용자의 ID 가져오기
		MemberEntity member = usersRepository.findByMemberId(loggedInUserId);
		String companyUrl = member.getCompany().getCompanyUrl();

		model.addAttribute("loggedInUserId", loggedInUserId); // 모델에 추가하여 뷰에 전달
		model.addAttribute("companyUrl", companyUrl);

		return "main/board/MessageForm";
	}


	@PostMapping("write")
	public String write(@ModelAttribute MessageDTO messageDTO,
						@AuthenticationPrincipal AuthenticatedUser user,
						@RequestParam("upload") MultipartFile upload) {

		try {

			messageService.write(messageDTO, uploadPath, upload);

			return "redirect:/";
		} catch (Exception e) {
			e.printStackTrace();
			return "main/board/MessageForm";
		}
	}

	@GetMapping("SentMessage")
	public String sentlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		List<MessageDTO> sentMessage = messageService.getsentListAll(user.getMemberId());
		model.addAttribute("sentMessageList", sentMessage);
		return "main/board/SentMessage";
	}

	@GetMapping("ReceivedMessage")
	public String receivedlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {

		List<MessageDTO> receivedMessage = messageService.getreceivedListAll(user.getMemberId());
		model.addAttribute("receivedMessageList", receivedMessage);
		return "main/board/ReceivedMessage";
	}

	@GetMapping("DeletedMessage")
	public String deltedlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		List<MessageDTO> deletedMessageList = messageService.getdeletedListAll(user.getMemberId());
		model.addAttribute("deletedMessageList", deletedMessageList);
		return "main/board/DeletedMessage";
	}


	@GetMapping("readReceived")
	public String readreceived(Model model, @RequestParam("messageNum") int messageNum) {
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new IllegalArgumentException("Invalid message ID"));
		try {
			MessageDTO messageDTO = messageService.getBoard(messageNum);

			DepartmentEntity departmentEntity = departmentRepository.findByDepartmentNum(message.getDepartment().getDepartmentNum());
			SubDepartmentEntity subdepartmentEntity = subDepartmentRepository.findBySubdepNum(message.getSubdepartment().getSubdepNum());

			model.addAttribute("departmentName", departmentEntity.getDepartmentName());
			model.addAttribute("subdepartmentName", subdepartmentEntity.getSubdepName());

			model.addAttribute("message", messageDTO);

			return "main/board/ReadReceived";
		} catch (Exception e) {
			return "redirect:/";
		}

	}

	@GetMapping("readSent")
	public String readsent(Model model, @RequestParam("messageNum") int messageNum) {
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new IllegalArgumentException("Invalid message ID"));
		try {
			MessageDTO messageDTO = messageService.getBoard(messageNum);

			DepartmentEntity departmentEntity = departmentRepository.findByDepartmentNum(message.getDepartment().getDepartmentNum());
			SubDepartmentEntity subdepartmentEntity = subDepartmentRepository.findBySubdepNum(message.getSubdepartment().getSubdepNum());

			model.addAttribute("departmentName", departmentEntity.getDepartmentName());
			model.addAttribute("subdepartmentName", subdepartmentEntity.getSubdepName());

			model.addAttribute("message", messageDTO);

			return "main/board/ReadSent";
		} catch (Exception e) {
			return "redirect:/";
		}

	}

	@GetMapping("updateReadStatus")
	@ResponseBody
	public String updateReadStatus(@RequestParam("messageNum") int messageNum) {
		try {
			messageService.updateReadStatus(messageNum);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@GetMapping("updateDeleteStatus")
	@ResponseBody
	public String updateDeleteStatus(@RequestParam("messageNum") int messageNum) {
		try {
			messageService.updateDeleteStatus(messageNum);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@GetMapping("restoreMessage")
	@ResponseBody
	public String restoreMessage(@RequestParam("messageNum") int messageNum) {
		try {
			messageService.restoreMessage(messageNum);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@GetMapping("delete")
	public String delete(@RequestParam("messageNum") int messageNum) {
		try {
			messageService.delete(messageNum);
			return "main/board/Message";
		} catch (Exception e) {
			e.printStackTrace();
			return "main/board/MessageForm";
		}
	}

	@GetMapping("download")
	public void download(
			@RequestParam("messageNum") Integer messageNum
			, HttpServletResponse response) {

		messageService.download(messageNum, response, uploadPath);
	}

	// 부서 선택 기능용 url들
	@GetMapping("departments")
	@ResponseBody
	public List<DepartmentDTO> getDepartmentsByCompanyUrl(@RequestParam("companyUrl") String companyUrl) {
		List<DepartmentEntity> departments = messageService.getDepartmentsByCompanyUrl(companyUrl);
		return departments.stream()
				.map(dept -> DepartmentDTO.builder()
						.departmentNum(dept.getDepartmentNum())
						.departmentName(dept.getDepartmentName())
						.build())
				.collect(Collectors.toList());
	}

	@GetMapping("subdepartments")
	@ResponseBody
	public List<SubDepartmentDTO> getSubDepartments(@RequestParam("departmentNum") int departmentNum) {
		// 서브 부서 목록을 가져온다
		List<SubDepartmentEntity> subDepartments = messageService.getSubDepartmentsByDepartmentNum(departmentNum);
		// 엔티티를 DTO로 변환하고 반환한다
		return subDepartments.stream()
				.map(subDept -> SubDepartmentDTO.builder()
						.subdepNum(subDept.getSubdepNum())
						.subdepName(subDept.getSubdepName())
						.build())
				.collect(Collectors.toList());
	}


	@CrossOrigin(origins = "http://localhost::8080")
	@GetMapping("members")
	@ResponseBody
	public List<MemberDTO> getMembers(@RequestParam("subdepNum") int subdepNum) {
		List<MemberDTO> members = messageService.getMembersBySubDep(subdepNum);
		return members.stream()
				.map(member -> MemberDTO.builder()
						.memberId(member.getMemberId())
						.memberName(member.getMemberName())
						.build())
				.collect(Collectors.toList());
	}

	//답장용 get post

		@GetMapping("reply")
		public String replyMessage(@RequestParam("messageNum") int messageNum, Model model, @AuthenticationPrincipal UserDetails userDetails) {
			// 메시지 번호를 기반으로 메시지 조회
			MessageEntity message = messageRepository.findByMessageNum(messageNum);

			String currentUserId = userDetails.getUsername();

			// 발신자 정보 및 부서, 하위부서 정보를 DTO에 담아 전달
			MessageDTO replyMessage = new MessageDTO();
			replyMessage.setReceiverUserId(message.getSender().getMemberId()); // 발신자는 자동으로 수신자로 설정
			replyMessage.setSenderUserId(currentUserId);
			replyMessage.setCompanyUrl(message.getCompany().getCompanyUrl());
			replyMessage.setDepartmentNum(message.getDepartment().getDepartmentNum());
			replyMessage.setSubdepNum(message.getSubdepartment().getSubdepNum());
			replyMessage.setTitle("Re: " + message.getTitle()); // 제목에 'Re:' 붙여서 답장으로 표시
			replyMessage.setContent("\n\n--- 원본 메시지 ---\n" + message.getContent());

			DepartmentEntity departmentEntity = departmentRepository.findByDepartmentNum(message.getDepartment().getDepartmentNum());
			SubDepartmentEntity subdepartmentEntity = subDepartmentRepository.findBySubdepNum(message.getSubdepartment().getSubdepNum());

			model.addAttribute("departmentName", departmentEntity.getDepartmentName());
			model.addAttribute("subdepartmentName", subdepartmentEntity.getSubdepName());

			model.addAttribute("replyMessage", replyMessage);

			return "main/board/replyMessageForm";  // 답장 작성 페이지로 이동
		}


	@PostMapping("sendReply")
	public String sendreply(@ModelAttribute MessageDTO messageDTO, @RequestParam("upload") MultipartFile upload) {
		String uploadPath = "/path/to/upload";

		try {
			messageService.sendReply(messageDTO, uploadPath, upload);

			return "redirect:/";

		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/";

		}
	}
}
