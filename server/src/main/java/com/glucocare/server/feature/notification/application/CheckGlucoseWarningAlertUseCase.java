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

/**
 * 혈당 경고 알림 확인 및 전송 Use Case
 * <p>
 * 주기적으로 모든 환자들의 최신 혈당 데이터를 확인하고, 간병인이 설정한 혈당 알림 정책에 따라 FCM 푸시 알림을 전송합니다.
 * 스케줄링을 통해 5분마다 실행되며, 중복 알림 전송을 방지하기 위해 알림 히스토리를 관리합니다.
 */
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

    /**
     * 모든 환자의 혈당 경고 알림 주기적 확인 및 전송
     * <p>
     * 비즈니스 로직 순서:
     * 1. 데이터베이스에서 모든 환자 목록 조회
     * 2. 각 환자별로:
     *    - 해당 환자의 간병인 관계 목록 조회
     *    - 최신 혈당 기록 조회
     *    - 혈당 기록이 없으면 건너뛰기
     * 3. 각 간병인 관계별로 혈당 알림 정책 확인 및 알림 전송
     *
     * @Scheduled 어노테이션을 통해 300초(300000ms)마다 자동 실행
     */
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

    /**
     * 간병인 관계의 혈당 알림 정책 확인 및 알림 전송
     * <p>
     * 처리 단계:
     * 1. 간병인 관계에 혈당 알림 정책이 설정되어 있는지 확인 (없으면 반환)
     * 2. 간병인 회원에게 FCM 토큰이 등록되어 있는지 확인 (없으면 반환)
     * 3. 해당 혈당 기록에 대한 알림이 이미 전송되었는지 확인 (이미 전송되었으면 반환)
     * 4. 모든 조건을 만족하면 혈당 경고 알림 전송
     *
     * @param memberPatientRelation 간병인 관계 엔티티
     * @param glucoseHistory        확인할 혈당 기록 엔티티
     */
    private void checkCareGiverRelationsGlucoseAlertPolicy(MemberPatientRelation memberPatientRelation, GlucoseHistory glucoseHistory) {
        var member = memberPatientRelation.getMember();
        if (!glucoseAlertPolicyRepository.existsByMemberPatientRelation(memberPatientRelation)) return;
        if (!fcmTokenRepository.existsByMember(member)) return;
        if (glucoseWarningNotificationHistoryRepository.existsByMemberAndGlucoseHistory(member, glucoseHistory)) return;
        sendGlucoseWarningAlert(memberPatientRelation, glucoseHistory);
    }

    /**
     * 혈당 경고 알림 FCM 전송
     * <p>
     * 처리 단계:
     * 1. 간병인 관계의 혈당 알림 정책 조회
     * 2. 간병인 회원의 FCM 토큰 조회
     * 3. 혈당 수치에 따라 경고 수준 판단:
     *    - 매우 고위험 기준값 이상: VERY_HIGH_RISK 알림 전송 및 히스토리 저장 후 반환
     *    - 고위험 기준값 이상: HIGH_RISK 알림 전송 및 히스토리 저장 후 반환
     *    - 그 외: NORMAL 히스토리만 저장 (알림 미전송)
     *
     * @param memberPatientRelation 간병인 관계 엔티티
     * @param glucoseHistory        혈당 기록 엔티티
     * @throws ApplicationException 알림 정책이나 FCM 토큰을 찾을 수 없는 경우 (NOT_FOUND)
     */
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
