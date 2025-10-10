package com.glucocare.server.client;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareGiver;
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
 * Firebase Cloud Messaging(FCM) 푸시 알림을 전송하는 클라이언트 클래스
 * <p>
 * 이 클래스는 Firebase SDK를 사용하여 간병인에게 환자의 혈당 경고 알림을
 * 푸시 메시지로 전송하는 기능을 제공합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FCMClient {
    /**
     * FCM 푸시 메시지를 전송하는 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 혈당 경고 타입에 따른 알림 제목 생성
     * 2. 환자 정보와 혈당 수치를 포함한 알림 본문 생성
     * 3. FCM 메시지 객체 생성 및 Firebase를 통해 전송
     * 4. 전송 성공 시 로그 기록
     *
     * @param fcmToken           수신자의 FCM 토큰 정보
     * @param careGiver          간병인 정보
     * @param glucoseHistory     전송할 혈당 기록
     * @param glucoseWarningType 혈당 경고 타입 (VERY_HIGH_RISK, HIGH_RISK, NORMAL)
     * @throws ApplicationException FCM 메시지 전송 실패 시
     */
    public void sendFCMMessage(FCMToken fcmToken, CareGiver careGiver, GlucoseHistory glucoseHistory, GlucoseWarningType glucoseWarningType) {
        try {
            var title = getTitle(glucoseWarningType);
            var body = getBody(careGiver, glucoseHistory);
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
            throw new ApplicationException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * 혈당 경고 타입에 따른 알림 제목을 생성하는 메서드
     * <p>
     * 혈당 경고 수준에 따라 적절한 한글 제목을 반환합니다.
     * NORMAL 타입의 경우 null을 반환합니다.
     *
     * @param glucoseWarningType 혈당 경고 타입
     * @return 알림 제목 문자열 (VERY_HIGH_RISK: "GluCoCare 혈당 고위험수치 알림 ", HIGH_RISK: "GluCoCare 혈당 위험수치 알림", NORMAL: null)
     */
    private String getTitle(GlucoseWarningType glucoseWarningType) {
        return switch (glucoseWarningType) {
            case VERY_HIGH_RISK -> "GluCoCare 혈당 고위험수치 알림 ";
            case HIGH_RISK -> "GluCoCare 혈당 위험수치 알림";
            default -> null;
        };
    }

    /**
     * 환자 정보와 혈당 수치를 포함한 알림 본문을 생성하는 메서드
     * <p>
     * 환자의 이름과 현재 혈당 수치(SGV)를 조합하여 알림 본문 메시지를 생성합니다.
     *
     * @param careGiver      간병인 엔티티 (환자 정보 포함)
     * @param glucoseHistory 혈당 기록 엔티티 (혈당 수치 포함)
     * @return 알림 본문 문자열 (형식: "{환자명}님의 혈당 수치가 {혈당값}입니다.")
     */
    private String getBody(CareGiver careGiver, GlucoseHistory glucoseHistory) {
        var patient = careGiver.getPatient();
        return patient.getName() + "님의 혈당 수치가 " + glucoseHistory.getSgv() + "입니다.";
    }
}
