package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();  // 메시지 전송 시간

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;  // 메시지 삭제 여부

    // 필요시 생성자 추가
    public ChatMessageEntity(ChatRoomEntity chatRoom, MemberEntity member, String message) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.message = message;
    }
}
