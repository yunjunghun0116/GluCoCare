package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.domain.RelationType;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.patient.dto.ReadPatientResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 환자 정보 조회 Use Case
 * <p>
 * 특정 회원에게 등록된 환자 정보를 조회합니다.
 * 한 회원은 하나의 환자 관계(PATIENT 타입)만 가질 수 있습니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadPatientUseCase {
    private final MemberRepository memberRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    /**
     * 환자 정보 조회
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 해당 회원의 환자 관계(PATIENT 타입) 목록 조회
     * 3. 환자 관계가 없으면 예외 발생
     * 4. 첫 번째 환자 관계에서 환자 엔티티 추출
     * 5. 환자 정보를 응답 DTO로 변환하여 반환
     *
     * @param memberId 조회를 요청한 회원의 ID
     * @return 환자 ID, 환자 이름, CGM 서버 URL을 포함한 응답
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND), 환자 관계가 없는 경우 (NOT_FOUND)
     */
    public ReadPatientResponse execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var relations = memberPatientRelationRepository.findByMemberAndRelationType(member, RelationType.PATIENT);
        if (relations.isEmpty()) {
            throw new ApplicationException(ErrorMessage.NOT_FOUND);
        }

        var relation = relations.getFirst();
        var patient = relation.getPatient();
        return ReadPatientResponse.of(patient.getId(), patient.getName(), patient.getCgmServerUrl());
    }
}
