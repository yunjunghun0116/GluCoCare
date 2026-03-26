package com.glucocare.server.feature.mission.domain;

import lombok.Getter;

@Getter
public enum MissionType {
    TIME_IN_RANGE("혈당 범위 지키기", "목표 혈당 범위를 잘 유지해서 포인트를 획득했어요!"),
    NO_HYPOGLYCEMIA("저혈당 피하기", "저혈당 없는 하루를 달성해 포인트를 획득했어요!"),
    NO_HYPERGLYCEMIA("고혈당 피하기", "고혈당 없는 하루를 달성해 포인트를 획득했어요!"),
    MORNING_NORMAL("오전 혈당 안정 유지", "오전 혈당을 안정적으로 유지해 포인트를 획득했어요!"),
    STABLE_GLUCOSE("혈당 안정성 유지", "안정적인 혈당 패턴을 유지해 포인트를 획득했어요!");

    private final String displayName;
    private final String rewardMessage;

    MissionType(String displayName, String rewardMessage) {
        this.displayName = displayName;
        this.rewardMessage = rewardMessage;
    }

}
