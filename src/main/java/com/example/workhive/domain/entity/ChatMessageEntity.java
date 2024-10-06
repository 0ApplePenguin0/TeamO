package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Builder 패턴을 사용하기 위해 추가
@Entity
@Table(name = "chat")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;  // 메시지 ID

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoomEntity chatRoom;  // 채팅방 (외래키)

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;  // 발신자 (외래키)

    @Column(name = "message", length = 255, nullable = false)
    private String message;  // 메시지 내용

    @Column(name = "image_url", length = 255)  // 이미지 URL 추가
    private String imageUrl;  // 메시지에 연결된 이미지 URL

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;  // 메시지 전송 시간

    // 메시지 저장 시 sentAt 기본값을 설정하는 로직 추가
    @PrePersist
    protected void onCreate() {
        if (this.sentAt == null) {
            this.sentAt = LocalDateTime.now();
        }
    }

    // 필요시 생성자 추가
    public ChatMessageEntity(ChatRoomEntity chatRoom, MemberEntity member, String message, String imageUrl) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.message = message;
        this.imageUrl = imageUrl; // 이미지 URL 추가
        this.sentAt = LocalDateTime.now(); // 메시지 전송 시간 기본값 설정
    }
}
