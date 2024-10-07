package com.example.workhive.domain.entity.Approval;

import com.example.workhive.domain.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", nullable = false)
    @ToString.Exclude
    private ApprovalEntity approval;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
}
