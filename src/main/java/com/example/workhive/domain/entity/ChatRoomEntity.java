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
 * 채팅룸 정보 Entity
 */
@Entity
@Table(name = "chat_room")  // DB 테이블 이름과 일치
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")  // DB 컬럼명과 일치
    private Integer chatRoomId;

    @Column(name = "company_url", length = 255, nullable = false)  // DB 컬럼명과 일치
    private String companyUrl;

    @Column(name = "department_id", length = 50)  // DB 컬럼명과 일치
    private String departmentId;

    @Column(name = "subdep_id", length = 50)  // DB 컬럼명과 일치
    private String subDepId;

    @Column(name = "project_num", length = 50)  // DB 컬럼명과 일치
    private String projectNum;

    @Column(name = "createdby_id", length = 50, nullable = false)  // DB 컬럼명과 일치
    private String createdById;

    @Column(name = "chatroom_name", length = 255, nullable = false)  // DB 컬럼명과 일치
    private String chatRoomName;

    @Column(name = "remarks", length = 255)  // DB 컬럼명과 일치
    private String remarks;
}
