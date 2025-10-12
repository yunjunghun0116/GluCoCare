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

/**
 * FCM 토큰 생성 Use Case
 * <p>
 * FCM 토큰을 생성하거나 업데이트합니다.
 * FCM 토큰은 푸시 알림 전송을 위해 필요한 기기별 고유 식별자입니다.
 * 기존 토큰이 있으면 업데이트하고, 없으면 새로 생성합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateFCMTokenUseCase {
    private final MemberRepository memberRepository;
    private final FCMTokenRepository fcmTokenRepository;

    /**
     * FCM 토큰 생성 또는 업데이트
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID와 FCM 토큰 값으로 FCM 토큰 생성 또는 업데이트
     * 2. 생성/업데이트된 FCM 토큰 정보를 응답 DTO로 변환하여 반환
     *
     * @param memberId FCM 토큰을 생성/업데이트할 회원의 ID
     * @param request  FCM 토큰 값을 포함한 생성 요청
     * @return FCM 토큰 ID와 토큰 값을 포함한 응답
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND)
     */
    public CreateFCMTokenResponse execute(Long memberId, CreateFCMTokenRequest request) {
        var token = createFCMTokenWithRequest(memberId, request);
        return createFCMTokenResponse(token);
    }

    /**
     * FCM 토큰 엔티티 생성 또는 업데이트
     * <p>
     * 처리 단계:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 해당 회원의 기존 FCM 토큰 존재 여부 확인
     * 3. 기존 토큰이 있으면:
     *    - 기존 토큰 엔티티 조회
     *    - 토큰 값 업데이트
     *    - 데이터베이스에 저장
     * 4. 기존 토큰이 없으면:
     *    - 새로운 FCM 토큰 엔티티 생성
     *    - 데이터베이스에 저장
     * 5. 생성/업데이트된 FCM 토큰 엔티티 반환
     *
     * @param memberId FCM 토큰을 생성/업데이트할 회원의 ID
     * @param request  FCM 토큰 값을 포함한 생성 요청
     * @return 생성/업데이트된 FCM 토큰 엔티티
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND)
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
     * FCM 토큰 엔티티를 응답 DTO로 변환
     * <p>
     * 처리 단계:
     * 1. FCM 토큰 엔티티에서 ID와 토큰 값 추출
     * 2. 응답 DTO 생성
     * 3. 생성된 응답 DTO 반환
     *
     * @param fcmToken 변환할 FCM 토큰 엔티티
     * @return FCM 토큰 ID와 토큰 값을 포함한 응답 객체
     */
    private CreateFCMTokenResponse createFCMTokenResponse(FCMToken fcmToken) {
        return CreateFCMTokenResponse.of(fcmToken.getId(), fcmToken.getFcmToken());
    }
}
