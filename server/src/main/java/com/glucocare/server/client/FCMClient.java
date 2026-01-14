package com.glucocare.server.client;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.notification.domain.FCMToken;
import com.glucocare.server.feature.notification.domain.GlucoseWarningType;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * FCM 푸시 알림 전송 클라이언트
 * <p>
 * Firebase SDK를 사용하여 간병인에게 환자의 혈당 경고 알림을 푸시 메시지로 전송합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FCMClient {
    /**
     * FCM 푸시 메시지 전송
     * <p>
     * 처리 단계:
     * 1. 혈당 경고 타입에 따른 알림 제목 생성
     * 2. 환자 정보와 혈당 수치를 포함한 알림 본문 생성
     * 3. FCM 메시지 객체 생성 (토큰, 제목, 본문 포함)
     * 4. Firebase를 통해 푸시 메시지 전송
     * 5. 전송 성공 시 로그 기록
     * 6. 전송 실패 시 에러 로그 기록 및 예외 발생
     *
     * @param fcmToken           수신자의 FCM 토큰 정보
     * @param careRelation       간병인 관계 엔티티 (환자 정보 포함)
     * @param glucoseHistory     혈당 기록 엔티티
     * @param glucoseWarningType 혈당 경고 타입 (VERY_HIGH_RISK, HIGH_RISK)
     * @throws ApplicationException FCM 메시지 전송 실패 시 (INTERNAL_SERVER_ERROR)
     */
    public void sendFCMMessage(FCMToken fcmToken, CareRelation careRelation, GlucoseHistory glucoseHistory, GlucoseWarningType glucoseWarningType) {
        try {
            var title = getTitle(glucoseWarningType);
            var body = getBody(careRelation, glucoseHistory);
            var message = Message.builder()
                                 .setToken(fcmToken.getFcmToken())
                                 .setNotification(Notification.builder()
                                                              .setTitle(title)
                                                              .setBody(body)
                                                              .build())
                                 .build();
            FirebaseMessaging.getInstance()
                             .send(message);
            log.info("Title : {}, Body : {} 메시지 전송 완료", title, body);
        } catch (Exception e) {
            log.error("FCM 메시지 전송 실패: {}", e.getMessage(), e);
            throw new ApplicationException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 혈당 경고 타입에 따른 알림 제목 생성
     * <p>
     * 처리 단계:
     * 1. 혈당 경고 타입에 따라 switch 문으로 분기
     * 2. VERY_HIGH_RISK: "GluCoCare 혈당 고위험수치 알림 " 반환
     * 3. HIGH_RISK: "GluCoCare 혈당 위험수치 알림" 반환
     * 4. 그 외(NORMAL): null 반환
     *
     * @param glucoseWarningType 혈당 경고 타입
     * @return 알림 제목 문자열 또는 null
     */
    private String getTitle(GlucoseWarningType glucoseWarningType) {
        return switch (glucoseWarningType) {
            case VERY_HIGH_RISK -> "GluCoCare 혈당 고위험수치 알림 ";
            case HIGH_RISK -> "GluCoCare 혈당 위험수치 알림";
            default -> null;
        };
    }

    /**
     * 환자 정보와 혈당 수치를 포함한 알림 본문 생성
     * <p>
     * 처리 단계:
     * 1. 간병인 관계에서 환자 엔티티 추출
     * 2. 환자 이름과 혈당 수치(SGV)를 조합하여 본문 생성
     * 3. 생성된 본문 반환
     *
     * @param careRelation   간병인 관계 엔티티 (환자 정보 포함)
     * @param glucoseHistory 혈당 기록 엔티티 (혈당 수치 포함)
     * @return 알림 본문 문자열 (형식: "{환자명}님의 혈당 수치가 {혈당값}입니다.")
     */
    private String getBody(CareRelation careRelation, GlucoseHistory glucoseHistory) {
        var patient = careRelation.getPatient();
        return patient.getName() + "님의 혈당 수치가 " + glucoseHistory.getSgv() + "입니다.";
    }
}
