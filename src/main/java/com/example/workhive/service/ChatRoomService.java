package com.example.workhive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.workhive.domain.dto.ChatRoomDTO;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.domain.entity.CompanyEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.ProjectMemberEntity;
import com.example.workhive.domain.entity.ChatRoomKindEntity;
import com.example.workhive.repository.ChatRoomKindRepository;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.repository.MemberRepository;
import com.example.workhive.repository.ProjectMemberRepository;
import com.example.workhive.repository.CompanyRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final ProjectMemberRepository projectMemberRepository;

    // 채팅방 목록 불러오기
    public List<String> getChatRoomsByUser(String memberId) {
        // projectMember 테이블에서 memberId와 일치하는 모든 projectMember 엔티티를 가져옴
        List<ProjectMemberEntity> projectMembers = projectMemberRepository.findByMember_MemberId(memberId);

        // 채팅방 이름을 저장할 리스트
        List<String> chatRoomNames = new ArrayList<>();

        // projectMember에서 가져온 채팅방 ID로 채팅방 이름을 조회
        for (ProjectMemberEntity projectMember : projectMembers) {
            ChatRoomEntity chatRoom = projectMember.getChatRoom();
            if (chatRoom != null) {
                chatRoomNames.add(chatRoom.getChatRoomName());
            }
        }

        return chatRoomNames;
    }


 // 채팅방 생성하기
    public void createChatRoom(ChatRoomDTO chatRoomDTO) {
        // DTO에서 필요한 엔티티를 조회
        // MemberEntity는 항상 유효한 memberId로 존재한다고 가정 (Optional 사용)
        MemberEntity member = memberRepository.findByMemberId(chatRoomDTO.getCreatedByMemberId());

        // CompanyEntity도 항상 유효한 companyId로 존재한다고 가정 (Optional에서 값을 꺼냄)
        CompanyEntity company = companyRepository.findByCompanyId(chatRoomDTO.getCompanyId())
                                                 .orElseThrow(() -> new IllegalArgumentException("해당 회사가 없습니다."));

        ChatRoomKindEntity chatRoomKind = new ChatRoomKindEntity();
        chatRoomKind.setChatroomKindId(1L);  // 이미 생성된 '프로젝트' 채팅방 종류 사용
        chatRoomKind.setKind("프로젝트");

        // ChatRoomEntity 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName(chatRoomDTO.getChatRoomName())
                .createdByMember(member) // 생성자 MemberEntity 설정
                .company(company) // 회사 CompanyEntity 설정
                .chatRoomKind(chatRoomKind) // 채팅방 종류 ChatRoomKindEntity 설정
                .build();

        // 채팅방 저장
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom); // 저장된 ChatRoomEntity 반환받음

        // 생성된 채팅방의 chatRoomId와 생성자의 memberId로 project_member 테이블에 추가
        ProjectMemberEntity projectMember = new ProjectMemberEntity();
        projectMember.setChatRoom(savedChatRoom);  // 저장된 채팅방의 chatRoomId 사용
        projectMember.setMember(member);  // 생성자의 memberId 사용
        projectMember.setRole("방장");  // 생성자는 방장으로 설정

        // project_member 테이블에 데이터 저장
        projectMemberRepository.save(projectMember);
    }

}
