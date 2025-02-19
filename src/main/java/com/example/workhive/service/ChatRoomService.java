package com.example.workhive.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ChatRoomKindRepository chatRoomKindRepository;

    // 채팅방 목록 불러오기 (채팅방 이름과 ID 함께 반환)
    public List<ChatRoomDTO> getChatRoomsByUser(String memberId) {
        List<ProjectMemberEntity> projectMembers = projectMemberRepository.findByMember_MemberId(memberId);
        List<ChatRoomDTO> chatRoomDTOs = new ArrayList<>();

        if (projectMembers != null && !projectMembers.isEmpty()) {
            for (ProjectMemberEntity projectMember : projectMembers) {
                ChatRoomEntity chatRoom = projectMember.getChatRoom();
                if (chatRoom != null) {
                    ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .chatRoomName(chatRoom.getChatRoomName())
                        .createdByMemberId(chatRoom.getCreatedByMember().getMemberId())
                        .build();
                    chatRoomDTOs.add(chatRoomDTO);
                }
            }
        }

        return chatRoomDTOs;  // 빈 목록 반환
    }
 // 사용자가 채팅방에서 나가는 기능
    public boolean leaveChatRoom(Long chatRoomId, String memberId) {
        try {
            // 해당 채팅방과 사용자의 project_member 엔티티를 찾음
            Optional<ProjectMemberEntity> projectMember = projectMemberRepository.findByChatRoom_ChatRoomIdAndMember_MemberId(chatRoomId, memberId);

            if (projectMember.isPresent()) {
                // 데이터 삭제 (채팅방 나가기)
                projectMemberRepository.delete(projectMember.get());
                return true;
            } else {
                return false;  // 해당 사용자가 채팅방에 없을 경우
            }
        } catch (Exception e) {
            log.error("Error while leaving chat room", e);
            return false;
        }
    }

    // 채팅방 생성하기 (프로젝트 채팅방)
    public Long createProjectChatRoom(ChatRoomDTO chatRoomDTO) {
        // MemberEntity는 항상 유효한 memberId로 존재한다고 가정
        MemberEntity member = memberRepository.findByMemberId(chatRoomDTO.getCreatedByMemberId());
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // CompanyEntity도 항상 유효한 companyId로 존재한다고 가정
        CompanyEntity company = companyRepository.findByCompanyId(chatRoomDTO.getCompanyId());
        if (company == null) {
            throw new IllegalArgumentException("존재하지 않는 회사입니다.");
        }

        // 프로젝트 채팅방 종류를 조회하여 설정
        ChatRoomKindEntity chatRoomKind = chatRoomKindRepository.findByKind("프로젝트")
            .orElseThrow(() -> new IllegalArgumentException("프로젝트 종류를 찾을 수 없습니다."));

        // 새로운 채팅방 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
            .chatRoomName(chatRoomDTO.getChatRoomName())
            .createdByMember(member) // 생성자 MemberEntity 설정
            .company(company) // 회사 CompanyEntity 설정
            .chatRoomKind(chatRoomKind) // 채팅방 종류 ChatRoomKindEntity 설정
            .build();

        // 채팅방 저장
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);

        // 생성된 채팅방의 chatRoomId와 생성자의 memberId로 project_member 테이블에 추가
        ProjectMemberEntity projectMember = ProjectMemberEntity.builder()
            .chatRoom(savedChatRoom)  // 저장된 채팅방의 chatRoomId 사용
            .member(member)  // 생성자의 memberId 사용
            .role("방장")  // 생성자는 방장으로 설정
            .build();

        projectMemberRepository.save(projectMember);
        
        // 생성된 채팅방 ID 반환
        return savedChatRoom.getChatRoomId();
    }

    // 채팅방 참여자 목록 가져오기
    public List<String> getChatRoomParticipants(Long chatRoomId) {
        List<ProjectMemberEntity> projectMembers = projectMemberRepository.findByChatRoom_ChatRoomId(chatRoomId);
        List<String> participantNames = new ArrayList<>();

        for (ProjectMemberEntity memberEntity : projectMembers) {
            participantNames.add(memberEntity.getMember().getMemberName());
        }

        return participantNames;
    }

    // 사용자 초대 기능
    public boolean inviteUserToChatRoom(Long chatRoomId, String memberId) {
        try {
            MemberEntity member = memberRepository.findByMemberId(memberId);
            ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없습니다."));

            ProjectMemberEntity projectMember = ProjectMemberEntity.builder()
                .chatRoom(chatRoom)
                .member(member)
                .role("일반")  // 초대된 사용자의 기본 역할
                .build();

            projectMemberRepository.save(projectMember);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
