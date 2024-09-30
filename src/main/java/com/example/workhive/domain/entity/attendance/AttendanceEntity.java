package com.example.workhive.domain.entity.attendance;

import com.example.workhive.domain.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="attendance_id")
    private Long attendanceId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendance_member"))
    private MemberEntity member;

    @Column(name="check_in")
    private LocalDateTime checkIn;

    @Column(name="check_out")
    private LocalDateTime checkOut;

    @Column(name="status")
    private String status;

    @Column(name = "attendance_date")
    private LocalDate attendanceDate;
}
