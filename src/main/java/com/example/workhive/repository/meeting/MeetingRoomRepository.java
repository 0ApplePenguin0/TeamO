package com.example.workhive.repository.meeting;

import com.example.workhive.domain.entity.MeetingRoom.MeetingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoomEntity, Long> {
    List<MeetingRoomEntity> findByCompanyCompanyId(Long companyId);
}
