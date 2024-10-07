//package com.example.workhive.controller;
//
//import org.springframework.ui.Model;
//import com.example.workhive.domain.dto.MemberDTO;
//import com.example.workhive.service.MemberService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.security.Principal;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("board")
//public class MyPageController {
//
//    private final MemberService memberService;
//
//    @GetMapping("/edit")
//    public String showProfileEditForm(Model model, Principal principal) {
//        String memberId = principal.getName();  // 현재 로그인한 사용자 ID
//        MemberDTO member = memberService.getMemberById(memberId);
//        model.addAttribute("member", member);
//        return "profile/edit";
//    }
//
//    @PostMapping("/edit")
//    public String updateProfile(@ModelAttribute("member") MemberDTO memberDTO,
//                                @RequestParam("profileImage") MultipartFile profileImage,
//                                RedirectAttributes redirectAttributes) {
//        try {
//            memberService.updateProfile(memberDTO, profileImage);
//            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile.");
//        }
//        return "redirect:/profile/edit";
//    }
//}
//}
