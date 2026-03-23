package com.glucocare.server.feature.notification.application;

import com.glucocare.server.client.FcmClient;
import com.glucocare.server.feature.notification.domain.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduledSendSilentPushUseCase {

    private final FcmClient fcmClient;
    private final FcmTokenRepository fcmTokenRepository;

    @Scheduled(fixedDelay = 300000)
    public void execute() {
        var tokens = fcmTokenRepository.findAll();
        if (tokens.isEmpty()) return;
        fcmClient.sendSilentSyncMessages(tokens);
    }
}
