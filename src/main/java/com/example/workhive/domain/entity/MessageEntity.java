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
@Table(name = "Message")
public class MessageEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_num")
    private Integer messageNum;

    @ManyToOne
    @JoinColumn(name = "sender", referencedColumnName = "member_id", nullable = false)
    private MemberEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver", referencedColumnName = "member_id", nullable = false)
    private MemberEntity receiver;
    
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;
    
    @CreationTimestamp
    @Column(name = "sent_time", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime sentTime;

    @Column(name = "read_chk", columnDefinition = "tinyint(1)")
    boolean readChk;

    @Column(name = "delete_status", columnDefinition = "tinyint(1)")
    boolean deleteStatus;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Column(name = "original_name", length = 300)
    private String originalName;

    @Column(name = "file_name", length = 100)
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "company_url", referencedColumnName = "company_url", nullable = false)
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name = "department_num", referencedColumnName = "department_num")
    private DepartmentEntity department;

    @ManyToOne
    @JoinColumn(name = "subdep_num", referencedColumnName = "subdep_num")
    private SubDepartmentEntity subdepartment;

}

