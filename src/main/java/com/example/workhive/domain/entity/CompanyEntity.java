package com.example.workhive.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companys")
public class CompanyEntity {
    @Id
    @Column(name = "company_url", length = 255)
    private String companyUrl;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "company_adress", nullable = false, length = 300)
    private String companyAdress;

}

