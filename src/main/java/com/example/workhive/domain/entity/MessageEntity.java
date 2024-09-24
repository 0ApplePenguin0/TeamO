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
    @Column(name = "message_id") // message_num에서 message_id로 변경
    private Long messageId; // Integer에서 Long으로 변경

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "member_id", nullable = false) // sender에서 sender_id로 변경
    private MemberEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "member_id", nullable = false) // receiver에서 receiver_id로 변경
    private MemberEntity receiver;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 255) // length를 1000에서 255로 변경
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", columnDefinition = "timestamp default current_timestamp") // sent_time에서 sent_at으로 변경
    private LocalDateTime sentAt; // sentTime에서 sentAt으로 변경

    @Column(name = "is_read", columnDefinition = "tinyint(1) default 0") // read_chk에서 is_read로 변경
    private boolean isRead; // readChk에서 isRead로 변경

    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0") // delete_status에서 is_deleted로 변경
    private boolean isDeleted; // deleteStatus에서 isDeleted로 변경

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

}
