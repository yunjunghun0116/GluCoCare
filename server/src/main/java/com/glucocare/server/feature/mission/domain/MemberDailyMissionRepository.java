package com.glucocare.server.feature.mission.domain;

import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MemberDailyMissionRepository extends JpaRepository<MemberDailyMission, Long> {
    List<MemberDailyMission> findByMemberAndMissionDate(Member member, LocalDate date);

    Boolean existsByMemberAndMissionDate(Member member, LocalDate missionDate);
}
