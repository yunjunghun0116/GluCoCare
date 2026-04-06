package com.glucocare.server.feature.mission.dto;

import com.glucocare.server.feature.mission.domain.MemberDailyMission;
import com.glucocare.server.feature.mission.domain.MissionType;

import java.time.LocalDate;

public record DailyMissionResponse(
        Long id,
        String title,
        String description,
        MissionType missionType,
        Double threshold,
        Double currentValue,
        Boolean canComplete,
        Boolean isCompleted,
        Boolean isFailed,
        Long rewardPoint,
        LocalDate missionDate
) {
    public static DailyMissionResponse from(MemberDailyMission dailyMission, Boolean canComplete, Double currentValue) {
        var mission = dailyMission.getMission();
        return new DailyMissionResponse(dailyMission.getId(),
                                        mission.getTitle(),
                                        mission.getDescription(),
                                        mission.getMissionType(),
                                        mission.getThreshold(),
                                        currentValue,
                                        canComplete,
                                        dailyMission.getIsCompleted(),
                                        dailyMission.getIsFailed(),
                                        dailyMission.getMission()
                                                    .getRewardPoint(),
                                        dailyMission.getDate());
    }
}
