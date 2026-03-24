package com.glucocare.server.feature.mission.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.mission.domain.MemberDailyMissionRepository;
import com.glucocare.server.feature.mission.domain.MissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompleteDailyMissionUseCase {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final MemberRepository memberRepository;
    private final MemberDailyMissionRepository memberDailyMissionRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final MissionValidator missionValidator;

    public void execute(Long id, Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var dailyMission = memberDailyMissionRepository.findById(id)
                                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (dailyMission.getIsCompleted() || dailyMission.getIsFailed()) return;
        var today = LocalDate.now(ZONE);
        var records = getTodayRecords(member, today);
        if (missionValidator.validate(dailyMission, records)) {
            dailyMission.complete();
        }
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
