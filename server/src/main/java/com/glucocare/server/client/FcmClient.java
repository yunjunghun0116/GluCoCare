package com.glucocare.server.client;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.notification.domain.FcmToken;
import com.glucocare.server.feature.notification.domain.FcmTokenRepository;
import com.glucocare.server.feature.notification.domain.GlucoseWarningType;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmClient {

    private final FcmTokenRepository fcmTokenRepository;

    public void sendFcmMessage(FcmToken fcmToken, CareRelation careRelation, GlucoseHistory glucoseHistory, GlucoseWarningType glucoseWarningType) {
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
            fcmTokenRepository.delete(fcmToken);
            throw new ApplicationException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }

    private String getTitle(GlucoseWarningType glucoseWarningType) {
        return switch (glucoseWarningType) {
            case VERY_HIGH_RISK -> "GluCoCare 혈당 고위험수치 알림 ";
            case HIGH_RISK -> "GluCoCare 혈당 위험수치 알림";
            default -> "GluCoCare 혈당 정상범위 알림";
        };
    }

    private String getBody(CareRelation careRelation, GlucoseHistory glucoseHistory) {
        var patient = careRelation.getPatient();
        return patient.getName() + "님의 혈당 수치가 " + glucoseHistory.getSgv() + "입니다.";
    }
}
