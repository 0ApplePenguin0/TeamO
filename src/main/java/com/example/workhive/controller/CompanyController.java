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

   /**
    * 관리자 정보 입력하는 페이지로 이동
    * @param model
    * @param user
    * @param session
    * @return
    */
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

   /**
    * 관리자 정보 폼 제출받아 저장
    * @param memberDetailDTO
    * @param companyId
    * @param session
    * @return
    */
   @PostMapping("saveAdminDetail")
   public String saveAdminDetail(@ModelAttribute MemberDetailDTO memberDetailDTO, long companyId, HttpSession session) {

      companyService.registerAdmin(memberDetailDTO, companyId);


      return "redirect:/main/board";
   }

   /**
    * 회사 등록 저장
    * @param companyData
    * @param model
    * @param session
    * @param user
    * @return
    */
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

}