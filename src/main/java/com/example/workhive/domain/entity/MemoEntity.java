package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "memo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MemoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memo_id")
    private Integer memoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", referencedColumnName = "member_id")  // MemberEntity의 memberId 필드 참조
    private MemberEntity member;  // 외래키로 MemberEntity 참조

    @Column(nullable = false)
    private String memoContent;

    @CreatedDate
    @Column(name = "created_at", columnDefinition= "timestamp default current_timestamp")
    private LocalDateTime createdAt;
}