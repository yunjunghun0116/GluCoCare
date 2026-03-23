package com.glucocare.server.client;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.notification.domain.FcmToken;
import com.glucocare.server.feature.notification.domain.FcmTokenRepository;
import com.glucocare.server.feature.notification.domain.GlucoseWarningType;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmClient {

    private final FcmTokenRepository fcmTokenRepository;

    public void sendFcmMessage(FcmToken fcmToken, CareRelation careRelation, GlucoseHistory glucoseHistory, GlucoseWarningType glucoseWarningType) {
        try {
            var title = getTitle(glucoseWarningType);
            var body = getBody(careRelation, glucoseHistory, glucoseWarningType);
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

    public void sendSilentSyncMessages(List<FcmToken> fcmTokens) {
        try {
            var tokens = fcmTokens.stream()
                                  .map(FcmToken::getFcmToken)
                                  .toList();

            var partitions = new ArrayList<List<String>>();
            for (int i = 0; i < tokens.size(); i += 500) {
                partitions.add(tokens.subList(i, Math.min(i + 500, tokens.size())));
            }

            for (List<String> partition : partitions) {
                MulticastMessage message = MulticastMessage.builder()
                                                           .addAllTokens(partition)
                                                           .setApnsConfig(ApnsConfig.builder()
                                                                                    .setAps(Aps.builder()
                                                                                               .setContentAvailable(true)
                                                                                               .build())
                                                                                    .build())
                                                           .putData("type", "sync")
                                                           .build();

                BatchResponse response = FirebaseMessaging.getInstance()
                                                          .sendEachForMulticast(message);
                log.info("Silent Push 전송 완료: 성공 {}, 실패 {}", response.getSuccessCount(), response.getFailureCount());
            }
        } catch (Exception e) {
            log.error("Silent Push 전송 실패: {}", e.getMessage(), e);
        }
    }

    private String getTitle(GlucoseWarningType glucoseWarningType) {
        return switch (glucoseWarningType) {
            case VERY_HIGH_RISK -> "GluCoCare 고혈당 매우 위험 알림";
            case HIGH_RISK -> "GluCoCare 고혈당 위험 알림";
            case LOW_RISK -> "GluCoCare 저혈당 위험 알림";
            default -> "GluCoCare 혈당 정상범위 알림";
        };
    }

    private String getSuggestion(GlucoseWarningType glucoseWarningType) {
        return switch (glucoseWarningType) {
            case VERY_HIGH_RISK -> "곧 혈당이 고혈당 매우 위험 수치에 도달할 수 있으니 고강도 운동을 수행해야 해요.";
            case HIGH_RISK -> "곧 혈당이 고혈당 위험 수치에 도달할 수 있으니 산책이나 가벼운 운동을 수행해야 해요.";
            case LOW_RISK -> "곧 혈당이 저혈당 위험 수치에 도달할 수 있으니 혈당을 높여줄 수 있는 음식을 섭취해야 해요.";
            default -> "GluCoCare 혈당 정상범위 알림";
        };
    }

    private String getBody(CareRelation careRelation, GlucoseHistory glucoseHistory, GlucoseWarningType glucoseWarningType) {
        var patient = careRelation.getPatient();
        return patient.getName() + "님의 혈당 수치가 " + glucoseHistory.getSgv() + "입니다. " + getSuggestion(glucoseWarningType);
    }
}
