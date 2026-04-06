package com.glucocare.server.feature.mission.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.mission.domain.MemberDailyMission;
import com.glucocare.server.feature.mission.domain.MemberDailyMissionRepository;
import com.glucocare.server.feature.mission.domain.MissionValidator;
import com.glucocare.server.feature.point.domain.PointHistory;
import com.glucocare.server.feature.point.domain.PointHistoryRepository;
import com.glucocare.server.feature.point.domain.PointTransactionType;
import com.glucocare.server.feature.point.domain.PointWallet;
import com.glucocare.server.feature.point.domain.PointWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompleteDailyMissionUseCase {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final MemberRepository memberRepository;
    private final MemberDailyMissionRepository memberDailyMissionRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final PointWalletRepository pointWalletRepository;
    private final PointHistoryRepository pointHistoryRepository;
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
            var pointWallet = pointWalletRepository.findByMember(member)
                                                   .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
            earnPoint(member, pointWallet, dailyMission);
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

    private void earnPoint(Member member, PointWallet pointWallet, MemberDailyMission dailyMission) {
        var amount = dailyMission.getMission()
                                 .getRewardPoint();
        var mission = dailyMission.getMission();
        var balanceAfter = pointWallet.getBalance() + dailyMission.getMission()
                                                                  .getRewardPoint();
        var pointHistory = new PointHistory(member,
                                            PointTransactionType.EARN,
                                            amount,
                                            balanceAfter,
                                            mission.getMissionType()
                                                   .getRewardMessage());
        pointWallet.earn(amount);
        pointHistoryRepository.save(pointHistory);
    }
}
