package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareGiverRepository;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicy;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.dto.ReadGlucoseAlertPolicyResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 혈당 경고 정책 조회를 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 특정 간병인에게 설정된 혈당 경고 정책을 조회하는 비즈니스 로직을 처리합니다.
 * 권한 검증을 통해 해당 회원이 요청한 간병인의 소유자인지 확인한 후 정책 정보를 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadGlucoseAlertPolicyUseCase {
    private final CareGiverRepository careGiverRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    /**
     * 혈당 경고 정책을 조회하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 careGiverId로 간병인 관계 조회
     * 2. 해당 간병인 관계가 요청한 회원의 것인지 권한 검증
     * 3. 간병인에게 설정된 혈당 경고 정책 조회
     * 4. 조회된 정책 정보를 응답 객체로 변환하여 반환
     *
     * @param memberId    혈당 경고 정책을 조회하려는 회원의 ID
     * @param careGiverId 혈당 경고 정책을 조회할 간병인의 ID
     * @return 혈당 경고 정책 정보를 포함한 응답 객체 (ID, 간병인 ID, 고위험 수치, 매우 고위험 수치)
     * @throws ApplicationException 간병인을 찾을 수 없거나, 권한이 없거나, 정책이 없는 경우
     */
    public ReadGlucoseAlertPolicyResponse execute(Long memberId, Long careGiverId) {
        var careGiver = careGiverRepository.findById(careGiverId)
                                           .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!careGiver.getMember()
                      .getId()
                      .equals(memberId)) throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findByCareGiver(careGiver)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return convertGlucoseAlertPolicyResponse(glucoseAlertPolicy);
    }

    /**
     * 혈당 경고 정책 엔티티를 응답 객체로 변환하는 메서드
     * <p>
     * 혈당 경고 정책 엔티티에서 필요한 정보(정책 ID, 간병인 ID, 고위험 수치, 매우 고위험 수치)를
     * 추출하여 클라이언트에게 반환할 응답 객체로 변환합니다.
     *
     * @param glucoseAlertPolicy 변환할 혈당 경고 정책 엔티티
     * @return 정책 ID, 간병인 ID, 고위험 기준값, 매우 고위험 기준값을 포함한 응답 객체
     */
    private ReadGlucoseAlertPolicyResponse convertGlucoseAlertPolicyResponse(GlucoseAlertPolicy glucoseAlertPolicy) {
        var careGiver = glucoseAlertPolicy.getCareGiver();
        return ReadGlucoseAlertPolicyResponse.of(glucoseAlertPolicy.getId(), careGiver.getId(), glucoseAlertPolicy.getHighRiskValue(), glucoseAlertPolicy.getVeryHighRiskValue());
    }
}
