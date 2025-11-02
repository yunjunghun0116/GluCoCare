package com.glucocare.server.feature.notification.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.notification.domain.FCMToken;
import com.glucocare.server.feature.notification.domain.FCMTokenRepository;
import com.glucocare.server.feature.notification.dto.CreateFCMTokenRequest;
import com.glucocare.server.feature.notification.dto.CreateFCMTokenResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateFCMTokenUseCase {
    private final MemberRepository memberRepository;
    private final FCMTokenRepository fcmTokenRepository;

    public CreateFCMTokenResponse execute(Long memberId, CreateFCMTokenRequest request) {
        var token = createFCMTokenWithRequest(memberId, request);
        return createFCMTokenResponse(token);
    }

    private FCMToken createFCMTokenWithRequest(Long memberId, CreateFCMTokenRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));

        var fcmToken = fcmTokenRepository.findByMember(member)
                                         .map(token -> {
                                             token.updateFCMToken(request.fcmToken());
                                             return token;
                                         })
                                         .orElseGet(() -> new FCMToken(member, request.fcmToken()));
        fcmTokenRepository.save(fcmToken);
        return fcmToken;
    }

    private CreateFCMTokenResponse createFCMTokenResponse(FCMToken fcmToken) {
        return CreateFCMTokenResponse.of(fcmToken.getId(), fcmToken.getFcmToken());
    }
}
