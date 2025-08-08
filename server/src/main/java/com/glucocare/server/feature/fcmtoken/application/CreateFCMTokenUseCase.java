package com.glucocare.server.feature.fcmtoken.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.fcmtoken.domain.FCMToken;
import com.glucocare.server.feature.fcmtoken.domain.FCMTokenRepository;
import com.glucocare.server.feature.fcmtoken.dto.CreateFCMTokenRequest;
import com.glucocare.server.feature.fcmtoken.dto.CreateFCMTokenResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * FCM 토큰 생성 및 업데이트 기능을 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 Firebase Cloud Messaging(FCM) 토큰을 생성하거나 업데이트하는
 * 비즈니스 로직을 처리합니다. FCM 토큰은 푸시 알림 전송을 위해 필요한
 * 기기별 고유 식별자입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateFCMTokenUseCase {
    private final MemberRepository memberRepository;
    private final FCMTokenRepository fcmTokenRepository;

    /**
     * FCM 토큰을 생성하거나 업데이트하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 회원에게 기존 FCM 토큰이 있는지 확인
     * 2. 기존 토큰이 있으면 업데이트, 없으면 새로 생성
     * 3. 생성 또는 업데이트된 FCM 토큰 정보를 응답 객체로 변환
     *
     * @param memberId FCM 토큰을 생성/업데이트할 회원의 ID
     * @param request  FCM 토큰 생성 요청 정보 (FCM 토큰 값 포함)
     * @return 생성되거나 업데이트된 FCM 토큰 정보를 담은 응답 객체
     * @throws ApplicationException 회원을 찾을 수 없는 경우
     */
    public CreateFCMTokenResponse execute(Long memberId, CreateFCMTokenRequest request) {
        var token = createFCMTokenWithRequest(memberId, request);
        return createFCMTokenResponse(token);
    }

    /**
     * FCM 토큰을 생성하거나 업데이트하는 내부 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 memberId로 회원 존재 여부 확인
     * 2. 해당 회원의 기존 FCM 토큰 존재 여부 확인
     * 3. 기존 토큰이 있으면 토큰 값 업데이트
     * 4. 기존 토큰이 없으면 새로운 FCM 토큰 엔티티 생성
     * 5. 변경사항을 데이터베이스에 저장
     *
     * @param memberId FCM 토큰을 생성/업데이트할 회원의 ID
     * @param request  FCM 토큰 생성 요청 정보
     * @return 생성되거나 업데이트된 FCM 토큰 엔티티
     * @throws ApplicationException 회원을 찾을 수 없는 경우
     */
    private FCMToken createFCMTokenWithRequest(Long memberId, CreateFCMTokenRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (fcmTokenRepository.existsByMember(member)) {
            var fcmToken = fcmTokenRepository.findByMember(member)
                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
            fcmToken.updateFCMToken(request.fcmToken());
            fcmTokenRepository.save(fcmToken);
            return fcmToken;
        }
        var fcmToken = new FCMToken(member, request.fcmToken());
        fcmTokenRepository.save(fcmToken);
        return fcmToken;
    }

    /**
     * FCM 토큰 엔티티를 응답 객체로 변환하는 메서드
     * <p>
     * FCM 토큰 엔티티에서 필요한 정보(토큰 ID, FCM 토큰 값)를 추출하여
     * 클라이언트에게 반환할 응답 객체를 생성합니다.
     *
     * @param fcmToken 변환할 FCM 토큰 엔티티
     * @return FCM 토큰 ID와 토큰 값을 포함한 응답 객체
     */
    private CreateFCMTokenResponse createFCMTokenResponse(FCMToken fcmToken) {
        return CreateFCMTokenResponse.of(fcmToken.getId(), fcmToken.getFcmToken());
    }
}
