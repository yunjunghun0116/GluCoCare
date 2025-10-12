package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.dto.ReadCareGiverRelationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 간병인 관계 단건 조회 Use Case
 * <p>
 * 특정 간병인 관계의 상세 정보를 조회합니다. 권한 검증을 통해 본인의 간병인 관계만 조회할 수 있도록 보장합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadCareGiverRelationUseCase {
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    /**
     * 간병인 관계 상세 조회 (권한 검증 포함)
     * <p>
     * 비즈니스 로직 순서:
     * 1. 간병인 관계 ID로 데이터베이스에서 간병인 관계 엔티티 조회
     * 2. 조회된 간병인 관계의 소유자(Member)와 요청한 회원 ID가 일치하는지 권한 검증
     * 3. 권한이 없으면 예외 발생 (다른 회원의 간병인 관계 조회 시도 차단)
     * 4. 권한이 있으면 간병인 관계 정보를 응답 DTO로 변환하여 반환
     *
     * @param memberId 조회를 요청한 회원의 ID (권한 검증용)
     * @param id 조회할 간병인 관계의 ID
     * @return 간병인 관계 ID, 환자 ID, 환자 이름을 포함한 응답
     * @throws ApplicationException 간병인 관계를 찾을 수 없는 경우 (NOT_FOUND), 권한이 없는 경우 (INVALID_ACCESS)
     */
    public ReadCareGiverRelationResponse execute(Long memberId, Long id) {
        var careGiverRelation = memberPatientRelationRepository.findById(id)
                                                               .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!careGiverRelation.getMember()
                              .getId()
                              .equals(memberId)) {
            throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        }
        return convertCareGiverResponse(careGiverRelation);
    }

    /**
     * 간병인 관계 엔티티를 응답 DTO로 변환
     * <p>
     * 처리 단계:
     * 1. 간병인 관계 엔티티에서 환자 엔티티 추출
     * 2. 간병인 관계 ID, 환자 ID, 환자 이름을 포함한 응답 DTO 생성
     * 3. 생성된 응답 DTO 반환
     *
     * @param memberPatientRelation 변환할 간병인 관계 엔티티
     * @return 간병인 관계 ID, 환자 ID, 환자 이름을 포함한 응답 객체
     */
    private ReadCareGiverRelationResponse convertCareGiverResponse(MemberPatientRelation memberPatientRelation) {
        var patient = memberPatientRelation.getPatient();
        return ReadCareGiverRelationResponse.of(memberPatientRelation.getId(), patient.getId(), patient.getName());
    }
}
