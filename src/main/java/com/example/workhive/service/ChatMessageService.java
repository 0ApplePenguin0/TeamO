package com.example.workhive.service;

import com.example.workhive.domain.dto.ChatMessageDTO;
import com.example.workhive.domain.entity.ChatMessageEntity;
import com.example.workhive.domain.entity.ChatRoomEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.repository.ChatMessageRepository;
import com.example.workhive.repository.ChatRoomRepository;
import com.example.workhive.repository.MemberRepository;

import jakarta.persistence.criteria.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths; // java.nio.file.Paths는 사용할 수 있지만, Path는 패키지 경로로 명시
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private static final String UPLOAD_DIR = "D:/tempUpload"; // 파일 저장 경로
    // 메시지 저장
    @Transactional
    public void saveMessage(ChatMessageDTO messageDTO) {
        // 채팅방과 멤버를 가져옴
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));
        MemberEntity member = memberRepository.findByMemberId(messageDTO.getMemberId());

        if (member == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }

        // ChatMessageEntity 생성 및 데이터 세팅
        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(messageDTO.getMessage())
                .imageUrl(messageDTO.getImageUrl()) // 이미지 URL 추가
                .build();

        // 메시지 저장
        chatMessageRepository.save(chatMessageEntity);
    }

    public String saveImage(MultipartFile image) throws IOException {
        // 업로드 디렉토리 경로를 확인하고 없으면 생성
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 디렉토리 생성
        }

        // 고유한 파일명을 생성하기 위해 UUID 사용
        String originalFilename = image.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // 저장할 파일의 경로 생성 (java.nio.file.Path를 명시적으로 사용)
        java.nio.file.Path filePath = java.nio.file.Paths.get(UPLOAD_DIR, uniqueFilename);
        
        // 파일 저장
        Files.copy(image.getInputStream(), filePath);

        // 저장된 파일의 URL 반환 (로컬 환경에서는 파일 경로 반환)
        return filePath.toString();
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatMessageDTO> getMessagesByChatRoom(Long chatRoomId) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoom_ChatRoomIdAndIsDeletedFalse(chatRoomId);
        List<ChatMessageDTO> messageDTOs = new ArrayList<>();

        for (ChatMessageEntity message : messages) {
            // Entity를 DTO로 변환
            ChatMessageDTO messageDTO = ChatMessageDTO.fromEntity(message);
            messageDTOs.add(messageDTO);
        }

        return messageDTOs;
    }
}
