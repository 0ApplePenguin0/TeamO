package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitation_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Long codeId;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_invitation_code_company"))
    private CompanyEntity company;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count", columnDefinition = "INT DEFAULT 0")
    private Integer usageCount;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by", foreignKey = @ForeignKey(name = "fk_invitation_code_members"))
    private MemberEntity createdBy;
}
