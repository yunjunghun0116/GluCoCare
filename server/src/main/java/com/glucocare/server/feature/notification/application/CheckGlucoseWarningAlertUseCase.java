package com.glucocare.server.feature.notification.application;

import com.glucocare.server.client.FCMClient;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
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
    private final MemberPatientRelationRepository memberPatientRelationRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;
    private final GlucoseWarningNotificationHistoryRepository glucoseWarningNotificationHistoryRepository;

    @Scheduled(fixedRate = 300000)
    public void execute() {
        var patients = patientRepository.findAll();

        for (var patient : patients) {
            var careGiverRelations = memberPatientRelationRepository.findAllByPatient(patient);
            var glucoseHistory = glucoseHistoryRepository.findFirstByPatientOrderByDateDesc(patient);
            if (glucoseHistory.isEmpty()) continue;
            for (var careGiverRelation : careGiverRelations) {
                checkCareGiverRelationsGlucoseAlertPolicy(careGiverRelation, glucoseHistory.get());
            }
        }
    }

    private void checkCareGiverRelationsGlucoseAlertPolicy(MemberPatientRelation memberPatientRelation, GlucoseHistory glucoseHistory) {
        var member = memberPatientRelation.getMember();
        if (!glucoseAlertPolicyRepository.existsByMemberPatientRelation(memberPatientRelation)) return;
        if (!fcmTokenRepository.existsByMember(member)) return;
        if (glucoseWarningNotificationHistoryRepository.existsByMemberAndGlucoseHistory(member, glucoseHistory)) return;
        sendGlucoseWarningAlert(memberPatientRelation, glucoseHistory);
    }

    private void sendGlucoseWarningAlert(MemberPatientRelation memberPatientRelation, GlucoseHistory glucoseHistory) {
        var member = memberPatientRelation.getMember();
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findByMemberPatientRelation(memberPatientRelation)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var fcmToken = fcmTokenRepository.findByMember(member)
                                         .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (glucoseHistory.getSgv() >= glucoseAlertPolicy.getVeryHighRiskValue()) {
            fcmClient.sendFCMMessage(fcmToken, memberPatientRelation, glucoseHistory, GlucoseWarningType.VERY_HIGH_RISK);
            var glucoseWarningNotification = new GlucoseWarningNotificationHistory(member, glucoseHistory, GlucoseWarningType.VERY_HIGH_RISK);
            glucoseWarningNotificationHistoryRepository.save(glucoseWarningNotification);
            return;
        }
        if (glucoseHistory.getSgv() >= glucoseAlertPolicy.getHighRiskValue()) {
            fcmClient.sendFCMMessage(fcmToken, memberPatientRelation, glucoseHistory, GlucoseWarningType.HIGH_RISK);
            var glucoseWarningNotification = new GlucoseWarningNotificationHistory(member, glucoseHistory, GlucoseWarningType.HIGH_RISK);
            glucoseWarningNotificationHistoryRepository.save(glucoseWarningNotification);
            return;
        }
        var glucoseWarningNotification = new GlucoseWarningNotificationHistory(member, glucoseHistory, GlucoseWarningType.NORMAL);
        glucoseWarningNotificationHistoryRepository.save(glucoseWarningNotification);

    }
}
