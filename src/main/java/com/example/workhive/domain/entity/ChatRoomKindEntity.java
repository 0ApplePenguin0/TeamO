package com.example.workhive.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 종류 Entity
 */
@Entity
@Table(name = "chatroom_kind")  // 데이터베이스 테이블 이름과 일치
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomKindEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_kind_id")  // DB 컬럼명과 일치
    private Long chatroomKindId;

    @Column(name = "kind", length = 50, nullable = false)  // DB 컬럼명과 일치
    private String kind;
}
