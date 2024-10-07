package com.example.workhive.domain.entity;

import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 회사 정보 Entity
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
public class CompanyEntity {

    @Id
    @ToString.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id", nullable = false)
    private Long companyId;  // company_id를 Long 타입으로 변경

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "company_address", nullable = false, length = 255)
    private String companyAddress;

    @Column(name ="company_url", length = 255)
    private String companyUrl;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // 회의실 목록을 toString에서 제외
    private List<MeetingRoomEntity> meetingRooms;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyEntity)) return false;
        CompanyEntity that = (CompanyEntity) o;
        return companyId != null && companyId.equals(that.getCompanyId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
