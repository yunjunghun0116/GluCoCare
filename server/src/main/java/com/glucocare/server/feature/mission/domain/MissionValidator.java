package com.glucocare.server.feature.mission.domain;

import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class MissionValidator {

    private static final int HYPO = 70; // 저혈당 기준
    private static final int HYPER = 180; // 고혈당 기준
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final LocalTime MORNING_START = LocalTime.of(8, 0);
    private static final LocalTime MORNING_END = LocalTime.of(12, 0);

    public Boolean validate(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        switch (dailyMission.getMission()
                            .getMissionType()) {
            case TIME_IN_RANGE -> {
                return validateTimeInRange(dailyMission, records);
            }
            case NO_HYPOGLYCEMIA -> {
                return validateNoHypo(dailyMission, records);
            }
            case NO_HYPERGLYCEMIA -> {
                return validateNoHyper(dailyMission, records);
            }
            case MORNING_NORMAL -> {
                return validateGoodMorning(dailyMission, records);
            }
            case STABLE_GLUCOSE -> {
                return validateGlucoseCV(dailyMission, records);
            }
            default -> {
                return false;
            }
        }
    }

    private Boolean validateTimeInRange(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        if (!canValidateTimeInRange(dailyMission, records)) return false;
        var ratio = records.stream()
                           .filter(g -> g.getSgv() >= HYPO && g.getSgv() <= HYPER)
                           .count() * 100.0 / records.size();
        return ratio >= dailyMission.getMission()
                                    .getThreshold();
    }

    private Boolean validateNoHypo(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        if (!canValidate(dailyMission, records)) return false;
        if (records.stream()
                   .anyMatch(glucose -> glucose.getSgv() < HYPO)) {
            dailyMission.fail();
            return false;
        }
        return true;
    }

    private Boolean validateNoHyper(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        if (!canValidate(dailyMission, records)) return false;
        if (records.stream()
                   .anyMatch(glucose -> glucose.getSgv() > HYPER)) {
            dailyMission.fail();
            return false;
        }
        return true;
    }

    private Boolean validateGoodMorning(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        var today = LocalDate.now(ZONE);
        var morningStart = today.atTime(MORNING_START)
                                .atZone(ZONE)
                                .toInstant()
                                .toEpochMilli();
        var morningEnd = today.atTime(MORNING_END)
                              .atZone(ZONE)
                              .toInstant()
                              .toEpochMilli();

        var morning = records.stream()
                             .filter(g -> g.getDateTime() >= morningStart && g.getDateTime() < morningEnd)
                             .toList();

        if (!canValidateGoodMorning(dailyMission, morning)) return false;

        return morning.stream()
                      .noneMatch(g -> g.getSgv() < HYPO || g.getSgv() > HYPER);
    }

    private Boolean validateGlucoseCV(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        if (!canValidate(dailyMission, records)) return false;
        var mean = records.stream()
                          .mapToInt(GlucoseHistory::getSgv)
                          .average()
                          .orElse(0);
        var std = Math.sqrt(records.stream()
                                   .mapToDouble(g -> Math.pow(g.getSgv() - mean, 2))
                                   .average()
                                   .orElse(0));
        var cv = (std / mean) * 100.0;
        return cv <= dailyMission.getMission()
                                 .getThreshold();
    }

    private Boolean canValidateTimeInRange(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        if (dailyMission.getIsFailed()) return false;
        if (!LocalTime.now(ZONE)
                      .isAfter(LocalTime.of(18, 0))) {
            return false;
        }
        return records.size() >= 100;
    }

    private Boolean canValidate(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        if (dailyMission.getIsFailed()) return false;
        return records.size() >= 100;
    }

    private Boolean canValidateGoodMorning(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        if (dailyMission.getIsFailed()) return false;
        return records.size() >= 30;
    }
}
