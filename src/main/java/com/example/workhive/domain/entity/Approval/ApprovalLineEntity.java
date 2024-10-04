package com.example.workhive.domain.entity.Approval;

import com.example.workhive.domain.entity.DepartmentEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.example.workhive.domain.entity.PositionEntity;
import com.example.workhive.domain.entity.TeamEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_line")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalLineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_line_id")
    private Long approvalLineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", nullable = false)
    @ToString.Exclude
    private ApprovalEntity approval;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private PositionEntity position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "status", length = 50)
    private String status = "PENDING";

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
}