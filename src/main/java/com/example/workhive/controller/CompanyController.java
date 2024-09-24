package com.example.workhive.controller;

import com.example.workhive.domain.dto.MemberDetailDTO;
import com.example.workhive.domain.dto.PositionDTO;
import com.example.workhive.domain.entity.PositionEntity;
import com.example.workhive.repository.CompanyRepository;
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

	@GetMapping("AdminRegister")
	public String adminregister(Model model, @AuthenticationPrincipal AuthenticatedUser user, HttpSession session	) {

		String companyIdStr = (String) session.getAttribute("companyId");
		Long companyId = null;

		companyId = Long.parseLong(companyIdStr);
		model.addAttribute("companyId", companyId);


		System.out.println(companyId);
		String loggedInUserId = user.getMemberId();

		model.addAttribute("loggedInUserId", loggedInUserId);
		
		return "main/company/AdminRegister";
	}

	@PostMapping("saveAdminDetail")
	public String saveAdminDetail(@ModelAttribute MemberDetailDTO memberDetailDTO, long companyId) {



		companyService.registerAdmin(memberDetailDTO, companyId);
		return "redirect:/main/board";
	}

	@GetMapping("Companyregister")
	public String companyregister(Model model, @AuthenticationPrincipal AuthenticatedUser user) {

		return "main/company/CompanyRegister";
	}

	@GetMapping("InvitationCodeInput")
	public String InvitationCodeInput(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
		return "main/company/InvitationCodeInput";
	}

	@GetMapping("EmployeeInfo")
	public String employeeinfo(Model model, @AuthenticationPrincipal AuthenticatedUser user, HttpSession session) {
		Long companyId = (Long) session.getAttribute("companyId");

		model.addAttribute("companyId", companyId);




		System.out.println(companyId);
		String loggedInUserId = user.getMemberId();

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
								   @RequestParam Long companyId,
								   HttpSession session) {
		String code = (String) session.getAttribute("code");
		System.out.println(code);
		companyService.registeremployee(memberDetailDTO, companyId, code);
		return "redirect:/main/board";
	}

	@PostMapping("saveCompany")
	public String saveCompany(@RequestParam Map<String, String> companyData,
							  Model model,
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
