package com.glucocare.server.feature.mission.domain;

import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MemberDailyMissionRepository extends JpaRepository<MemberDailyMission, Long> {
    @Query(
            """
            SELECT dm FROM MemberDailyMission dm
            JOIN FETCH dm.mission
            WHERE dm.member = :member
            AND dm.date = :date
            """
    )
    List<MemberDailyMission> findAllByMemberAndMissionDate(@Param("member") Member member, @Param("date") LocalDate date);
}
