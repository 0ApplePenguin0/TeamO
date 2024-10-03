package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MemoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memo_id")
    private Long memoId;  // 메모 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", referencedColumnName = "member_id", nullable = false)  // MemberEntity의 memberId 필드 참조
    private MemberEntity member;  // 작성자 외래키

    @Column(name = "content", nullable = false, length = 200)  // 메모 내용
    private String content;

    @CreatedDate
    @Column(name = "created_at", columnDefinition= "timestamp default current_timestamp")
    private LocalDateTime createdAt;  // 작성 시간


}
