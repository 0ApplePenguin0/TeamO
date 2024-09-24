package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity receiver;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false, columnDefinition = "boolean default false")
    private boolean isRead;

}
