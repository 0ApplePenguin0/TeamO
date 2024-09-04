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
@Table(name = "ChatRoom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChatRoomID")
    private Integer chatRoomId;

    @Column(name = "CompanyURL", length = 255, nullable = false)
    private String companyURL;

    @Column(name = "DepartmentID", length = 50)
    private String departmentId;

    @Column(name = "SubDepID", length = 50)
    private String subDepId;

    @Column(name = "ProjectNum", length = 50)
    private String projectNum;

    @Column(name = "CreatedByID", length = 50, nullable = false)
    private String createdById;

    @Column(name = "ChatRoomName", length = 255, nullable = false)
    private String chatRoomName;

    @Column(name = "Remarks", length = 255)
    private String remarks;
}
