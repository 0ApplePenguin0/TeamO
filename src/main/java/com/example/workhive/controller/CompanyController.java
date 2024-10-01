package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.dto.PositionDTO;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.PositionEntity;
import com.example.workhive.repository.CompanyRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.security.AuthenticatedUser;
import com.example.workhive.service.CompanyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/main/company")
public class CompanyController {

   private final CompanyService companyService;
   private final CompanyRepository companyRepository;
   private final MemberRepository usersRepository;

   @GetMapping("AdminRegister")
   public String adminregister(Model model, @AuthenticationPrincipal AuthenticatedUser user, HttpSession session) {

      String loggedInUserId = user.getMemberId();

      // Member 테이블에서 companyId 가져오기
      MemberEntity member = usersRepository.findByMemberId(loggedInUserId);

      Long companyId = member.getCompany().getCompanyId();
      model.addAttribute("companyId", companyId);

      if (member.getRole().name().equals("ROLE_ADMIN")
              || member.getRole().name().equals("ROLE_EMPLOYEE")
              || member.getRole().name().equals("ROLE_MANAGER")) {
         return "redirect:/main/board";
      }

      model.addAttribute("loggedInUserId", loggedInUserId);

      return "main/company/AdminRegister";
   }

   @PostMapping("saveAdminDetail")
   public String saveAdminDetail(@ModelAttribute MemberDetailDTO memberDetailDTO, long companyId, HttpSession session) {

      companyService.registerAdmin(memberDetailDTO, companyId);


      return "redirect:/main/board";
   }

   @GetMapping("Companyregister")
   public String companyregister(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
      String loggedInUserId = user.getMemberId();
      MemberEntity member = usersRepository.findById(loggedInUserId)
              .orElseThrow(() -> new RuntimeException("Member not found")); // 예외 처리

      // 회사 ID가 이미 등록되어 있는지 확인
      if (member.getCompany() != null) {
         // 이미 회사가 등록되어 있다면 adminregister로 리다이렉트
         return "redirect:/main/company/AdminRegister";
      }

      return "main/company/CompanyRegister"; // 회사 등록 폼으로 이동
   }

   @GetMapping("InvitationCodeInput")
   public String InvitationCodeInput(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
      String loggedInUserId = user.getMemberId();
      MemberEntity member = usersRepository.findByMemberId(loggedInUserId);

      if (member.getRole().name().equals("ROLE_ADMIN")
              || member.getRole().name().equals("ROLE_EMPLOYEE")
              || member.getRole().name().equals("ROLE_MANAGER")) {
         return "redirect:/main/board";
      }

      return "main/company/InvitationCodeInput";
   }

   @GetMapping("EmployeeInfo")
   public String employeeinfo(Model model, @AuthenticationPrincipal AuthenticatedUser user, HttpSession session) {
      Long companyId = (Long) session.getAttribute("companyId");

      model.addAttribute("companyId", companyId);

      System.out.println(companyId);
      String loggedInUserId = user.getMemberId();

      MemberEntity member = usersRepository.findByMemberId(loggedInUserId);

      if (member.getRole().name().equals("ROLE_ADMIN")
              || member.getRole().name().equals("ROLE_EMPLOYEE")
              || member.getRole().name().equals("ROLE_MANAGER")) {
         return "redirect:/main/board";
      }

      model.addAttribute("loggedInUserId", loggedInUserId);


      return "main/company/EmployeeInfo";

   }

   @PostMapping("validateInvitationCode")
   public String validateCompanyId(@RequestParam("code") String code, HttpSession session, Model model) {
      Long companyId = companyService.isValidInvitationCode(code);

      System.out.println(companyId);
      System.out.println(code);
      if (companyId != null) {
         // 유효한 경우, 세션에 companyId 저장
         session.setAttribute("companyId", companyId);
         session.setAttribute("code", code);

         return "redirect:/main/company/EmployeeInfo"; // 다음 페이지로 리다이렉트
      } else {
         // 유효하지 않은 경우, 에러 메시지와 함께 다시 입력 페이지로
         model.addAttribute("errorMessage", "유효하지 않은 초대 코드입니다.");
         return "main/company/InvitationCodeInput"; // 다시 입력 페이지로 돌아감
      }
   }

   @PostMapping("saveMemberDetail")
   public String saveMemberDetail(@ModelAttribute MemberDetailDTO memberDetailDTO,
                           @RequestParam("companyId") Long companyId,
                           HttpSession session) {
      String code = (String) session.getAttribute("code");
      System.out.println(code);
      companyService.registeremployee(memberDetailDTO, companyId, code);
      return "redirect:/main/board";
   }

   @PostMapping("saveCompany")
   public String saveCompany(@RequestParam Map<String, String> companyData,
                       Model model, //requestParam() 에서 ()를 넣으면 에러가 발생하는데 이유가 뭘까?
                       HttpSession session,
                       @AuthenticationPrincipal AuthenticatedUser user) {


      // companyData는 회사, 부서, 하위부서 정보를 포함한 모든 form data를 받습니다
      String loggedInUserId = user.getMemberId();
      companyData.put("memberId", loggedInUserId);
      boolean isSaved = companyService.saveCompanyAndDepartments(companyData);

      if (isSaved) {
         session.setAttribute("message", "회사와 부서 정보가 성공적으로 저장되었습니다.");
         session.setAttribute("companyId", companyData.get("companyId"));
         return "redirect:/main/company/AdminRegister";  // 저장 완료 후 다시 폼으로 리다이렉트
      } else {
         model.addAttribute("error", "회사와 부서 정보를 저장하는 데 문제가 발생했습니다.");

         return "main/company/CompanyRegister";
      }

   }

   @GetMapping("positions")
   @ResponseBody
   public List<PositionDTO> getPositionsCompanyId(@RequestParam("companyId") Long companyId) {
      // 회사 URL로 직급 목록을 조회
      List<PositionEntity> positions = companyService.getPositionsByCompanyId(companyId);
      // 위에서 받아온 리스트를 스트림으로 변환
      return positions.stream()
            .map(posi -> PositionDTO.builder()
                  .positionId(posi.getPositionId())
                  .positionName(posi.getPositionName())
                  .build())
            //변환된 DepartmentDTO객체들이 리스트로 모아져 반환됨
            .collect(Collectors.toList());
   }

}