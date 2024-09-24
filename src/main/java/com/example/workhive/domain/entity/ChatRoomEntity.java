package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 정보 Entity
 */
@Entity
@Table(name = "chatroom")  // DB 테이블 이름과 일치하도록 수정
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long chatRoomId;  // 채팅방 ID

    @ManyToOne
    @JoinColumn(name = "chatroom_kind_id", nullable = false)  // chatroom_kind 테이블 참조
    private ChatRoomKindEntity chatRoomKind;  // 채팅방 종류 (외래키)

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)  // company 테이블 참조
    private CompanyEntity company;  // 회사 (외래키)

    @ManyToOne
    @JoinColumn(name = "created_by_member_id", nullable = false)  // members 테이블 참조
    private MemberEntity createdByMember;  // 생성자 (외래키)

    @Column(name = "chatroom_name", length = 50, nullable = false)
    private String chatRoomName;  // 채팅방 이름
}
