package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 멤버 Entity
 */
@Entity
@Table(name = "project_member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long projectMemberId; // 프로젝트 멤버 ID (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)  // 채팅방 ID (Foreign Key)
    private ChatRoomEntity chatRoom;  // 채팅방 엔티티와 연관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)  // 멤버 ID (Foreign Key)
    private MemberEntity member;  // 멤버 엔티티와 연관

    @Column(name = "role", nullable = false, length = 50)
    private String role; // 역할 (방장, 부방장, 일반 등)
}
