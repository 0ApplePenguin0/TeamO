package com.example.workhive.domain.entity.Approval;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "approval_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalContentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", nullable = false)
    @ToString.Exclude
    private ApprovalEntity approval;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
}