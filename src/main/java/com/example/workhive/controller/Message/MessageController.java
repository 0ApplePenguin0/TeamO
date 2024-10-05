package com.example.workhive.controller.Message;

import com.example.workhive.domain.dto.MessageDTO;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.*;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
        return "main/board/ReceivedMessage";
    }

    /**
     * 받은 쪽지 내용 읽기 ( 요청되는 값 :  메세지 번호 )
     * @param model
     * @param messageId
     * @return
     */
    @GetMapping("readReceived")
    public String readreceived(Model model, @RequestParam("messageId") Long messageId) {
        // 쪽지 번호를 통해 쪽지 엔티티를 조회
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message ID"));
        try {
            // 쪽지 DTO를 조회
            MessageDTO messageDTO = messageService.getBoard(messageId);
            // 쪽지의 부서 및 하위 부서 정보를 조회
            MemberEntity sender = message.getSender();

            MemberDetailEntity senderDetail = memberDetailRepository.findByMember_MemberId(sender.getMemberId());

            DepartmentEntity departmentEntity = departmentRepository.findByDepartmentId(senderDetail.getDepartment().getDepartmentId());
            TeamEntity TeamEntity = TeamRepository.findByTeamId(senderDetail.getTeam().getTeamId());

            // 모델에 부서 및 하위 부서 이름과 쪽지 DTO를 추가하여 뷰에 전달
            model.addAttribute("departmentName", departmentEntity.getDepartmentName());
            model.addAttribute("teamName", TeamEntity.getTeamName());
            model.addAttribute("message", messageDTO);

            // 수신 쪽지 읽기 뷰로 이동
            return "main/board/ReadReceived";
        } catch (Exception e) {
            // 예외 발생 시 홈으로 리다이렉트
            e.printStackTrace();
            return "redirect:/";
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
        return "main/board/SentMessage";
    }

    /**
     * 보낸 쪽지 내용 읽기 ( 요청되는 값 :  메세지 번호 )
     * @param model
     * @param messageId
     * @return
     */
    @GetMapping("readSent")
    public String readsent(Model model, @RequestParam("messageId") Long messageId) {
        // 쪽지 번호를 통해 쪽지 엔티티를 조회
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message ID"));
        try {
            // 쪽지 DTO를 조회
            MessageDTO messageDTO = messageService.getBoard(messageId);
            // 쪽지의 부서 및 하위 부서 정보를 조회
            MemberEntity sender = message.getSender();

            MemberDetailEntity senderDetail = memberDetailRepository.findByMember_MemberId(sender.getMemberId());

            DepartmentEntity departmentEntity = departmentRepository.findByDepartmentId(senderDetail.getDepartment().getDepartmentId());
            TeamEntity TeamEntity = TeamRepository.findByTeamId(senderDetail.getTeam().getTeamId());

            // 모델에 부서 및 하위 부서 이름과 쪽지 DTO를 추가하여 뷰에 전달
            model.addAttribute("departmentName", departmentEntity.getDepartmentName());
            model.addAttribute("TeamName", TeamEntity.getTeamName());
            model.addAttribute("message", messageDTO);
            // 발신 쪽지 읽기 뷰로 이동
            return "main/board/ReadSent";
        } catch (Exception e) {
            // 예외 발생 시 홈으로 리다이렉트
            return "redirect:/";
        }

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
    @GetMapping("deletedMessage")
    public String deltedlistAll(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인한 사용자의 삭제된 쪽지 목록을 조회
        List<MessageDTO> deletedMessageList = messageService.getdeletedListAll(user.getMemberId());
        // 모델에 삭제된 쪽지 목록을 추가하여 뷰에 전달
        model.addAttribute("deletedMessageList", deletedMessageList);
        // 삭제된 쪽지 뷰로 이동
        return "main/board/DeletedMessage";
    }

    /**
     * 쪽지 삭제보관함으로 이동
     * (비동기 처리)(요청되는 값 : 메세지번호)
     * @param messageId
     * @return
     */
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

    /**
     * 삭제된 쪽지 복원
     * @param messageId
     * @return
     */
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

    /**
     * 쪽지 영구 삭제 처리
     * @param messageId
     * @return
     */
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
}
