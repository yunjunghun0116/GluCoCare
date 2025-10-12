package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicy;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.dto.ReadGlucoseAlertPolicyResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 혈당 알림 정책 조회 Use Case
 * <p>
 * 특정 간병인 관계에 설정된 혈당 알림 정책을 조회합니다.
 * 권한 검증을 통해 본인의 간병인 관계에 대한 정책만 조회할 수 있도록 보장합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadGlucoseAlertPolicyUseCase {
    private final MemberPatientRelationRepository memberPatientRelationRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    /**
     * 혈당 알림 정책 조회 (권한 검증 포함)
     * <p>
     * 비즈니스 로직 순서:
     * 1. 간병인 관계 ID로 데이터베이스에서 간병인 관계 엔티티 조회
     * 2. 조회된 간병인 관계의 소유자(Member)와 요청한 회원 ID가 일치하는지 권한 검증
     * 3. 권한이 없으면 예외 발생 (다른 회원의 정책 조회 시도 차단)
     * 4. 간병인 관계에 설정된 혈당 알림 정책 조회
     * 5. 조회된 정책 정보를 응답 DTO로 변환하여 반환
     *
     * @param memberId    조회를 요청한 회원의 ID (권한 검증용)
     * @param careGiverId 조회할 간병인 관계의 ID
     * @return 정책 ID, 간병인 관계 ID, 고위험 기준값, 매우 고위험 기준값을 포함한 응답
     * @throws ApplicationException 간병인 관계를 찾을 수 없는 경우 (NOT_FOUND), 권한이 없는 경우 (INVALID_ACCESS), 정책을 찾을 수 없는 경우 (NOT_FOUND)
     */
    public ReadGlucoseAlertPolicyResponse execute(Long memberId, Long careGiverId) {
        var careGiverRelation = memberPatientRelationRepository.findById(careGiverId)
                                                               .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!careGiverRelation.getMember()
                              .getId()
                              .equals(memberId)) throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findByMemberPatientRelation(careGiverRelation)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return convertGlucoseAlertPolicyResponse(glucoseAlertPolicy);
    }

    /**
     * 혈당 알림 정책 엔티티를 응답 DTO로 변환
     * <p>
     * 처리 단계:
     * 1. 정책 엔티티에서 간병인 관계 엔티티 추출
     * 2. 정책 ID, 간병인 관계 ID, 고위험 기준값, 매우 고위험 기준값을 포함한 응답 DTO 생성
     * 3. 생성된 응답 DTO 반환
     *
     * @param glucoseAlertPolicy 변환할 혈당 알림 정책 엔티티
     * @return 정책 ID, 간병인 관계 ID, 고위험 기준값, 매우 고위험 기준값을 포함한 응답 객체
     */
    private ReadGlucoseAlertPolicyResponse convertGlucoseAlertPolicyResponse(GlucoseAlertPolicy glucoseAlertPolicy) {
        var careGiverRelation = glucoseAlertPolicy.getMemberPatientRelation();
        return ReadGlucoseAlertPolicyResponse.of(glucoseAlertPolicy.getId(), careGiverRelation.getId(), glucoseAlertPolicy.getHighRiskValue(), glucoseAlertPolicy.getVeryHighRiskValue());
    }
}
