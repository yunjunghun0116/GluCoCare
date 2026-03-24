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
        Integer rewardPoint,
        LocalDate missionDate
) {
    public static DailyMissionResponse from(MemberDailyMission daily, Boolean canComplete, Double currentValue) {
        return new DailyMissionResponse(daily.getMission()
                                             .getId(),
                                        daily.getMission()
                                             .getTitle(),
                                        daily.getMission()
                                             .getDescription(),
                                        daily.getMission()
                                             .getMissionType(),
                                        daily.getMission()
                                             .getThreshold(),
                                        currentValue,
                                        canComplete,
                                        daily.getIsCompleted(),
                                        daily.getIsFailed(),
                                        daily.getMission()
                                             .getRewardPoint(),
                                        daily.getDate());
    }
}
