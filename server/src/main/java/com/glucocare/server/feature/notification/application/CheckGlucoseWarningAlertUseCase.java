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

/**
 * 혈당 경고 알림을 주기적으로 확인하고 전송하는 Use Case 클래스
 * <p>
 * 이 클래스는 스케줄링을 통해 주기적으로 모든 환자들의 최신 혈당 데이터를 확인하고,
 * 간병인이 설정한 혈당 경고 정책에 따라 FCM 푸시 알림을 전송하는 비즈니스 로직을 처리합니다.
 * 중복 알림 전송을 방지하기 위해 알림 히스토리를 관리합니다.
 */
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

    /**
     * 모든 환자의 혈당 경고 알림을 확인하고 전송하는 메인 메서드
     * <p>
     * 이 메서드는 5분마다 실행되며, 다음과 같은 과정을 수행합니다:
     * 1. 데이터베이스에서 모든 환자 목록 조회
     * 2. 각 환자별로 최신 혈당 기록 조회
     * 3. 해당 환자의 간병인들에게 설정된 혈당 경고 정책 확인
     * 4. 경고 정책 기준을 초과하는 경우 FCM 푸시 알림 전송
     * 5. 알림 전송 히스토리 저장
     *
     * @Scheduled 어노테이션을 통해 300초(300000ms)마다 자동 실행됩니다.
     */
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

    /**
     * 간병인의 혈당 경고 정책을 확인하고 알림을 전송하는 메서드
     * <p>
     * 이 메서드는 다음과 같은 검증을 수행합니다:
     * 1. 간병인에게 혈당 경고 정책이 설정되어 있는지 확인
     * 2. 간병인 회원에게 FCM 토큰이 등록되어 있는지 확인
     * 3. 해당 혈당 기록에 대한 알림이 이미 전송되었는지 확인
     * 4. 모든 조건을 만족하면 혈당 경고 알림 전송
     *
     * @param careGiver 간병인 엔티티
     * @param glucoseHistory 확인할 혈당 기록 엔티티
     */
    private void checkCareGiversGlucoseAlertPolicy(CareGiver careGiver, GlucoseHistory glucoseHistory) {
        var member = careGiver.getMember();
        if (!glucoseAlertPolicyRepository.existsByCareGiver(careGiver)) return;
        if (!fcmTokenRepository.existsByMember(member)) return;
        if (glucoseWarningNotificationHistoryRepository.existsByMemberAndGlucoseHistory(member, glucoseHistory)) return;
        sendGlucoseWarningAlert(careGiver, glucoseHistory);
    }

    /**
     * 혈당 경고 알림을 FCM을 통해 전송하는 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 간병인의 혈당 경고 정책 조회
     * 2. 간병인 회원의 FCM 토큰 조회
     * 3. 혈당 수치에 따라 경고 수준 판단 (VERY_HIGH_RISK, HIGH_RISK, NORMAL)
     * 4. 해당 경고 수준에 맞는 FCM 푸시 알림 전송
     * 5. 알림 전송 히스토리 저장 (중복 전송 방지용)
     *
     * @param careGiver 간병인 엔티티
     * @param glucoseHistory 혈당 기록 엔티티
     * @throws ApplicationException 경고 정책이나 FCM 토큰을 찾을 수 없는 경우
     */
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
