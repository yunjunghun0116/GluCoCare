package com.glucocare.server.feature.notification.application;

import com.glucocare.server.client.FcmClient;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.notification.domain.FcmTokenRepository;
import com.glucocare.server.feature.notification.domain.GlucoseWarningNotificationHistory;
import com.glucocare.server.feature.notification.domain.GlucoseWarningNotificationHistoryRepository;
import com.glucocare.server.feature.notification.domain.GlucoseWarningType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduledSendGlucoseWarningAlertUseCase {

    private final FcmClient fcmClient;
    private final MemberRepository memberRepository;
    private final CareRelationRepository careRelationRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;
    private final GlucoseWarningNotificationHistoryRepository glucoseWarningNotificationHistoryRepository;

    @Scheduled(fixedDelay = 300000)
    public void execute() {
        var patients = memberRepository.findAllByIsPatientTrue();
        if (patients.isEmpty()) return;

        var careRelations = careRelationRepository.findAllByPatientIn(patients);
        var relationsByPatient = careRelations.stream()
                                              .collect(Collectors.groupingBy(CareRelation::getPatient));

        var latestGlucoseMap = glucoseHistoryRepository.findLatestByPatient(patients)
                                                       .stream()
                                                       .collect(Collectors.toMap(GlucoseHistory::getPatient, gh -> gh));
        for (var patient : patients) {
            var glucoseHistory = latestGlucoseMap.get(patient);
            if (glucoseHistory == null) continue;

            var relations = relationsByPatient.getOrDefault(patient, List.of());
            for (var relation : relations) {
                checkCareGiverRelationsGlucoseAlertPolicy(relation, glucoseHistory);
            }
        }
    }

    private void checkCareGiverRelationsGlucoseAlertPolicy(CareRelation careRelation, GlucoseHistory glucoseHistory) {
        var member = careRelation.getMember();
        if (!glucoseAlertPolicyRepository.existsByCareRelation(careRelation)) return;
        if (!fcmTokenRepository.existsByMember(member)) return; // 토큰 없을 경우 알림을 보낼 수 없음
        if (glucoseWarningNotificationHistoryRepository.existsByMemberAndGlucoseHistory(member, glucoseHistory)) return; // 이미 해당 혈당에 대한 알림을 보냈을 경우 다시 보낼 수 없음
        sendGlucoseWarningAlert(careRelation, glucoseHistory);
    }

    private void sendGlucoseWarningAlert(CareRelation careRelation, GlucoseHistory glucoseHistory) {
        var member = careRelation.getMember();
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findByCareRelation(careRelation)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var fcmToken = fcmTokenRepository.findByMember(member)
                                         .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var warningType = GlucoseWarningType.from(glucoseHistory.getSgv(), glucoseAlertPolicy);
        if (warningType.isNeedSendNotification()) {
            fcmClient.sendFcmMessage(fcmToken, careRelation, glucoseHistory, warningType);
            var glucoseWarningNotification = new GlucoseWarningNotificationHistory(member, glucoseHistory, warningType);
            glucoseWarningNotificationHistoryRepository.save(glucoseWarningNotification);
        }
    }
}
