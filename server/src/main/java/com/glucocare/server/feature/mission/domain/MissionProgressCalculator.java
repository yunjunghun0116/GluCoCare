package com.glucocare.server.feature.mission.domain;

import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissionProgressCalculator {

    private static final int HYPO = 70;
    private static final int HYPER = 180;

    public Double calculate(MemberDailyMission dailyMission, List<GlucoseHistory> records) {
        if (records.isEmpty()) return 0.0;
        return switch (dailyMission.getMission()
                                   .getMissionType()) {
            case TIME_IN_RANGE -> records.stream()
                                         .filter(g -> g.getSgv() >= HYPO && g.getSgv() <= HYPER)
                                         .count() * 100.0 / records.size();
            case STABLE_GLUCOSE -> {
                var mean = records.stream()
                                  .mapToInt(GlucoseHistory::getSgv)
                                  .average()
                                  .orElse(0);
                var std = Math.sqrt(records.stream()
                                           .mapToDouble(g -> Math.pow(g.getSgv() - mean, 2))
                                           .average()
                                           .orElse(0));
                yield (std / mean) * 100.0;
            }
            default -> 0.0;
        };
    }
}
