package com.example.workhive.controller.Message;

import com.example.workhive.domain.dto.MessageDTO;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.*;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/main/message")
public class MessageController {

    private final MessageService messageService;
    private final MemberRepository usersRepository;
    private final MessageRepository messageRepository;
    private final DepartmentRepository departmentRepository;
    private final com.example.workhive.repository.TeamRepository TeamRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final FileRepository fileRepository;

    //${}를 통해 설정파일에서 값을 추출해서 uploadPath에 주입(파일 업로드용 경로)
    @Value("${main.board.uploadPath}")
    String uploadPath;

    @GetMapping("writeMessage")
    public String writeMessage(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인된 사용자의 ID 가져오기
        String loggedInUserId = user.getMemberId();
        // 해당 사용자의 멤버 엔티티를 조회
        MemberEntity member = usersRepository.findByMemberId(loggedInUserId);
        // 사용자의 회사 URL을 가져옴
        Long companyId = member.getCompany().getCompanyId();

        // 모델에 로그인된 사용자 ID와 회사 URL을 추가하여 뷰에 전달
        model.addAttribute("loggedInUserId", loggedInUserId); // 모델에 추가하여 뷰에 전달
        model.addAttribute("CompanyId", companyId);

        // 쪽지 작성 폼 뷰로 이동
        return "message/writeMessage";
    }


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


            return "redirect:/main/message/receivedMessage";
        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스 출력
            e.printStackTrace();
            // 에러 메시지와 함께 입력된 데이터 모델에 추가

            return "redirect:/main/message/receivedMessage";
        }
    }

    /**
     * 받은 쪽지함 페이지 이동
     * @param model
     * @param user
     * @return
     */
    @GetMapping("receivedMessage")
    public String receivedlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인한 사용자의 수신 쪽지 목록을 조회
        List<MessageDTO> receivedMessage = messageService.getreceivedListAll(user.getMemberId());
        // 모델에 수신 쪽지 목록을 추가하여 뷰에 전달
        model.addAttribute("receivedMessageList", receivedMessage);
        // 수신 쪽지 뷰로 이동
        return "message/receivedMessage";
    }


    @GetMapping("/readSent")
    @ResponseBody
    public ResponseEntity<?> readSent(@RequestParam("messageId") Long messageId) {
        try {
            // 쪽지 엔티티 조회
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid message ID"));

            // 쪽지 DTO 조회
            MessageDTO messageDTO = messageService.getBoard(messageId);

            // 수신자의 부서 및 하위 부서 정보 조회
            MemberEntity receiver = message.getReceiver(); // 수신자 정보
            MemberDetailEntity receiverDetail = memberDetailRepository.findByMember_MemberId(receiver.getMemberId());

            DepartmentEntity departmentEntity = departmentRepository.findByDepartmentId(receiverDetail.getDepartment().getDepartmentId());
            TeamEntity teamEntity = TeamRepository.findByTeamId(receiverDetail.getTeam().getTeamId());

            // 필요한 데이터를 JSON으로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("messageId", messageDTO.getMessageId());
            response.put("title", messageDTO.getTitle());
            response.put("receiverId", receiver.getMemberId()); // 수신자 ID로 변경
            response.put("departmentName", departmentEntity.getDepartmentName());
            response.put("teamName", teamEntity.getTeamName());
            response.put("sentAt", messageDTO.getSentAt());
            response.put("content", messageDTO.getContent());

            // JSON 응답 반환
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 오류 발생 시 400 에러 응답
            return ResponseEntity.status(400).body("Invalid request");
        }
    }

    @GetMapping("/readreceived")
    @ResponseBody
    public ResponseEntity<?> readreceived(@RequestParam("messageId") Long messageId) {
        try {
            // 쪽지 엔티티 조회
            MessageEntity message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid message ID"));

            // 쪽지 DTO 조회
            MessageDTO messageDTO = messageService.getBoard(messageId);

            // 발신자의 부서 및 하위 부서 정보 조회
            MemberEntity sender = message.getSender();
            MemberDetailEntity senderDetail = memberDetailRepository.findByMember_MemberId(sender.getMemberId());

            DepartmentEntity departmentEntity = departmentRepository.findByDepartmentId(senderDetail.getDepartment().getDepartmentId());
            TeamEntity teamEntity = TeamRepository.findByTeamId(senderDetail.getTeam().getTeamId());

            // 필요한 데이터를 JSON으로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("messageId", messageDTO.getMessageId());
            response.put("title", messageDTO.getTitle());
            response.put("senderId", sender.getMemberId());
            response.put("departmentName", departmentEntity.getDepartmentName());
            response.put("teamName", teamEntity.getTeamName());
            response.put("sentAt", messageDTO.getSentAt());
            response.put("content", messageDTO.getContent());

            // JSON 응답 반환
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 오류 발생 시 400 에러 응답
            return ResponseEntity.status(400).body("Invalid request");
        }
    }

    /**
     * 보낸 쪽지함 페이지 이동
     * @param model
     * @param user
     * @return
     */
    @GetMapping("sentMessage")
    public String sentlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인한 사용자의 발신 쪽지 목록을 조회
        List<MessageDTO> sentMessage = messageService.getsentListAll(user.getMemberId());
        // 모델에 발신 쪽지 목록을 추가하여 뷰에 전달
        model.addAttribute("sentMessageList", sentMessage);
        // 발신 쪽지 뷰로 이동
        return "message/sentMessage";
    }





    /**
     * 쪽지 읽음 상태 업데이트
     * (비동기 처리)(요청되는 값 : 메세지번호)
     * @param messageId
     * @return
     */
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

    /**
     * 쪽지 휴지통 페이지로 이동
     * @param model
     * @param user
     * @return
     */
    @GetMapping("recycleBin")
    public String recycleBin(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인한 사용자의 삭제된 쪽지 목록을 조회
        List<MessageDTO> deletedMessageList = messageService.getdeletedListAll(user.getMemberId());
        // 모델에 삭제된 쪽지 목록을 추가하여 뷰에 전달
        model.addAttribute("deletedMessageList", deletedMessageList);
        // 삭제된 쪽지 뷰로 이동
        return "message/recycleBin";
    }


    @PostMapping("updateDeleteStatus")
    @ResponseBody
    public String updateDeleteStatus(@RequestBody List<Long> messageIds) {
        try {
            // 쪽지 삭제 상태 업데이트
            for (Long messageId : messageIds) {
                messageService.updateDeleteStatus(messageId);
            }
            // 성공 응답
            return "success";
        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스 출력
            e.printStackTrace();
            // 실패 응답
            return "error";
        }
    }


    @PostMapping("restoreMessage")
    @ResponseBody
    public String restoreMessage(@RequestBody List<Long> messageIds) {
        try {
            for (Long messageId : messageIds) {
            messageService.restoreMessage(messageId);
            }
            // 성공 응답
            return "success";
        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스 출력
            e.printStackTrace();
            // 실패 응답
            return "error";
        }
    }


    @PostMapping("delete")
    @ResponseBody
    public String delete(@RequestBody List<Long> messageIds) {
        try {
            // 쪽지 완전삭제
            for (Long messageId : messageIds) {
                messageService.delete(messageId);
            }
            // 성공 응답
            return "success";
        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스 출력
            e.printStackTrace();
            // 실패 시 쪽지 작성 폼으로 이동
            return "error";
        }
    }


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
        return "message/ReplyMessage";  // 답장 작성 페이지로 이동
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
            return "message/sentMessage";

        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스 출력
            e.printStackTrace();
            // 실패 시 메인 페이지로 리다이렉트
            return "redirect:/";

        }
    }
}
