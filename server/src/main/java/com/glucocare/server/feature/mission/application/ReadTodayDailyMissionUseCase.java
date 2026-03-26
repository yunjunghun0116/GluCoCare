package com.glucocare.server.feature.mission.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.mission.domain.MemberDailyMission;
import com.glucocare.server.feature.mission.domain.MemberDailyMissionRepository;
import com.glucocare.server.feature.mission.domain.MissionProgressCalculator;
import com.glucocare.server.feature.mission.domain.MissionRepository;
import com.glucocare.server.feature.mission.domain.MissionValidator;
import com.glucocare.server.feature.mission.dto.DailyMissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadTodayDailyMissionUseCase {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final MemberRepository memberRepository;
    private final MissionRepository missionRepository;
    private final MemberDailyMissionRepository memberDailyMissionRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final MissionProgressCalculator missionProgressCalculator;
    private final MissionValidator missionValidator;

    public List<DailyMissionResponse> execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var today = LocalDate.now(ZONE);
        var dailyMissions = getDailyMissions(member, today);
        var records = getTodayRecords(member, today);

        return dailyMissions.stream()
                            .map(dailyMission -> DailyMissionResponse.from(dailyMission, missionValidator.validate(dailyMission, records), missionProgressCalculator.calculate(dailyMission, records)))
                            .toList();
    }

    private List<MemberDailyMission> getDailyMissions(Member member, LocalDate today) {
        var dailyMissions = memberDailyMissionRepository.findAllByMemberAndMissionDate(member, today);

        if (dailyMissions.isEmpty()) {
            dailyMissions = memberDailyMissionRepository.saveAll(missionRepository.findAllByIsActiveTrue()
                                                                                  .stream()
                                                                                  .map(mission -> new MemberDailyMission(member, mission, today))
                                                                                  .toList());
        }
        return dailyMissions;
    }

    private List<GlucoseHistory> getTodayRecords(Member member, LocalDate date) {
        var start = date.atStartOfDay(ZONE)
                        .toInstant()
                        .toEpochMilli();
        var end = date.plusDays(1)
                      .atStartOfDay(ZONE)
                      .toInstant()
                      .toEpochMilli();
        return glucoseHistoryRepository.findByPatientAndDateTimeBetweenOrderByDateTimeAsc(member, start, end);
    }
}
