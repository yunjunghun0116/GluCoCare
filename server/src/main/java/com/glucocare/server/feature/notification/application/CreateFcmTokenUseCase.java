package com.glucocare.server.feature.notification.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.notification.domain.FcmToken;
import com.glucocare.server.feature.notification.domain.FcmTokenRepository;
import com.glucocare.server.feature.notification.dto.CreateFCMTokenRequest;
import com.glucocare.server.feature.notification.dto.CreateFCMTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateFcmTokenUseCase {
    private final MemberRepository memberRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public CreateFCMTokenResponse execute(Long memberId, CreateFCMTokenRequest request) {
        var token = createFcmTokenWithRequest(memberId, request);
        return createFcmTokenResponse(token);
    }

    private FcmToken createFcmTokenWithRequest(Long memberId, CreateFCMTokenRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));

        var fcmToken = fcmTokenRepository.findByMember(member)
                                         .map(token -> {
                                             token.updateFcmToken(request.fcmToken());
                                             return token;
                                         })
                                         .orElseGet(() -> new FcmToken(member, request.fcmToken()));
        fcmTokenRepository.save(fcmToken);
        return fcmToken;
    }

    private CreateFCMTokenResponse createFcmTokenResponse(FcmToken fcmToken) {
        return CreateFCMTokenResponse.of(fcmToken.getId(), fcmToken.getFcmToken());
    }
}
