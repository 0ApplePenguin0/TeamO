package com.example.workhive.domain.entity.Approval;

import com.example.workhive.converter.MapToJsonConverter;
import com.example.workhive.domain.entity.CompanyEntity;
import com.example.workhive.domain.entity.MemberEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "approval")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    @ToString.Exclude
    private FormTemplateEntity formTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    private MemberEntity requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @ToString.Exclude
    private CompanyEntity company;

    @Column(name = "approval_status", length = 50)
    private String approvalStatus = "PENDING";

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    @Column(nullable = false)
    private String title;

/*    @Column(name = "content", columnDefinition = "JSON", nullable = false)
    private String content;*/
    @Convert(converter = MapToJsonConverter.class) // 추가된 부분
    @Column(name = "content", columnDefinition = "JSON", nullable = false)
    private Map<String, Object> content;

    @OneToMany(mappedBy = "approval", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalLineEntity> approvalLines;

    @OneToMany(mappedBy = "approval", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalHistoryEntity> approvalHistories;

    // equals와 hashCode는 approvalId만 사용
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApprovalEntity)) return false;
        ApprovalEntity that = (ApprovalEntity) o;
        return approvalId != null && approvalId.equals(that.getApprovalId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}