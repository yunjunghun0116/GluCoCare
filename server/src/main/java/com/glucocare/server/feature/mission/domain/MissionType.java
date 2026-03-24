package com.glucocare.server.feature.mission.domain;

public enum MissionType {
    TIME_IN_RANGE,      // 정상범위(70~180) 비율 N% 이상
    NO_HYPOGLYCEMIA,    // 저혈당(<70) 없는 하루
    NO_HYPERGLYCEMIA,   // 고혈당(>180) 없는 하루
    MORNING_NORMAL,     // 오전 8~12시 정상범위 유지 : TIR 비율
    STABLE_GLUCOSE      // 혈당 표준편차 N 이하
}
