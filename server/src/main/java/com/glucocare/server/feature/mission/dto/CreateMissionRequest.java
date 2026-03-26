package com.glucocare.server.feature.mission.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.glucocare.server.feature.mission.domain.MissionType;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateMissionRequest(
        @NotNull(message = "미션명은 반드시 입력되어야 합니다.") String title,
        @NotNull(message = "미션 소개는 반드시 입력되어야 합니다.") String description,
        @NotNull(message = "미션 유형은 반드시 입력되어야 합니다.") MissionType missionType,
        @NotNull(message = "기준값은 반드시 입력되어야 합니다.(없을경우 0.0 입력)") Double threshold,
        @NotNull(message = "보상 포인트는 반드시 입력되어야 합니다.") Long rewardPoint
) {
}
