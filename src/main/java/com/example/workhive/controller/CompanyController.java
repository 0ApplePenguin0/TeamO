package com.example.workhive.controller;

import com.example.workhive.domain.dto.PositionDTO;
import com.example.workhive.domain.entity.PositionEntity;
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
         return "redirect:/register/AdminRegister";  // 저장 완료 후 다시 폼으로 리다이렉트
      } else {
         model.addAttribute("error", "회사와 부서 정보를 저장하는 데 문제가 발생했습니다.");

         return "register/registerCompany";
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