package com.glucocare.server.feature.notification.application;

import com.glucocare.server.client.FCMClient;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareGiver;
import com.glucocare.server.feature.care.domain.CareGiverRepository;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.notification.domain.FCMTokenRepository;
import com.glucocare.server.feature.notification.domain.GlucoseWarningNotificationHistory;
import com.glucocare.server.feature.notification.domain.GlucoseWarningNotificationHistoryRepository;
import com.glucocare.server.feature.notification.domain.GlucoseWarningType;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckGlucoseWarningAlertUseCase {

    private final FCMClient fcmClient;
    private final PatientRepository patientRepository;
    private final CareGiverRepository careGiverRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;
    private final GlucoseWarningNotificationHistoryRepository glucoseWarningNotificationHistoryRepository;

    @Scheduled(fixedRate = 300000)
    public void execute() {
        var patients = patientRepository.findAll();

        for (var patient : patients) {
            var careGivers = careGiverRepository.findAllByPatient(patient);
            var glucoseHistory = glucoseHistoryRepository.findFirstByPatientOrderByDateDesc(patient);
            if (glucoseHistory.isEmpty()) continue;
            for (var careGiver : careGivers) {
                checkCareGiversGlucoseAlertPolicy(careGiver, glucoseHistory.get());
            }
        }
    }

    private void checkCareGiversGlucoseAlertPolicy(CareGiver careGiver, GlucoseHistory glucoseHistory) {
        var member = careGiver.getMember();
        if (!glucoseAlertPolicyRepository.existsByCareGiver(careGiver)) return;
        if (!fcmTokenRepository.existsByMember(member)) return;
        if (glucoseWarningNotificationHistoryRepository.existsByMemberAndGlucoseHistory(member, glucoseHistory)) return;
        sendGlucoseWarningAlert(careGiver, glucoseHistory);
    }

    private void sendGlucoseWarningAlert(CareGiver careGiver, GlucoseHistory glucoseHistory) {
        var member = careGiver.getMember();
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findByCareGiver(careGiver)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var fcmToken = fcmTokenRepository.findByMember(member)
                                         .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (glucoseHistory.getSgv() >= glucoseAlertPolicy.getVeryHighRiskValue()) {
            fcmClient.sendFCMMessage(fcmToken, careGiver, glucoseHistory, GlucoseWarningType.VERY_HIGH_RISK);
            var glucoseWarningNotification = new GlucoseWarningNotificationHistory(member, glucoseHistory, GlucoseWarningType.VERY_HIGH_RISK);
            glucoseWarningNotificationHistoryRepository.save(glucoseWarningNotification);
            return;
        }
        if (glucoseHistory.getSgv() >= glucoseAlertPolicy.getHighRiskValue()) {
            fcmClient.sendFCMMessage(fcmToken, careGiver, glucoseHistory, GlucoseWarningType.HIGH_RISK);
            var glucoseWarningNotification = new GlucoseWarningNotificationHistory(member, glucoseHistory, GlucoseWarningType.HIGH_RISK);
            glucoseWarningNotificationHistoryRepository.save(glucoseWarningNotification);
            return;
        }
        var glucoseWarningNotification = new GlucoseWarningNotificationHistory(member, glucoseHistory, GlucoseWarningType.NORMAL);
        glucoseWarningNotificationHistoryRepository.save(glucoseWarningNotification);

    }
}
