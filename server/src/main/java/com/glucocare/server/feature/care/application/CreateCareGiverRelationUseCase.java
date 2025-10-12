package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.*;
import com.glucocare.server.feature.care.dto.CreateCareGiverRelationRequest;
import com.glucocare.server.feature.care.dto.CreateCareGiverRelationResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 간병인 관계 생성 Use Case
 * <p>
 * 회원과 환자 간의 간병인 관계를 생성하고, 해당 관계에 대한 기본 혈당 알림 정책을 자동으로 설정합니다.
 * 간병인 관계가 생성되면 간병인은 환자의 혈당 데이터를 조회하고 알림을 받을 수 있습니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateCareGiverRelationUseCase {
    private final MemberRepository memberRepository;
    private final PatientRepository patientRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    /**
     * 간병인 관계 생성 및 혈당 알림 정책 초기화
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID와 환자 정보로 간병인 관계 엔티티 생성 및 저장
     * 2. 생성된 간병인 관계에 대한 기본 혈당 알림 정책 생성
     * 3. 생성된 간병인 관계 정보를 응답 객체로 변환하여 반환
     *
     * @param memberId 간병인 역할을 할 회원의 ID
     * @param request 환자 이름과 환자 ID를 포함한 간병인 관계 생성 요청
     * @return 생성된 간병인 관계 ID, 환자 ID, 환자 이름을 포함한 응답
     * @throws ApplicationException 회원이나 환자를 찾을 수 없는 경우 (NOT_FOUND), 이미 관계가 존재하는 경우 (ALREADY_EXISTS)
     */
    public CreateCareGiverRelationResponse execute(Long memberId, CreateCareGiverRelationRequest request) {
        var careGiverRelation = saveCareGiverWithRequest(memberId, request);
        createGlucoseAlertPolicy(careGiverRelation);
        return createCareGiverRelationResponse(careGiverRelation);
    }

    /**
     * 간병인 관계 엔티티 생성 및 데이터베이스 저장
     * <p>
     * 처리 단계:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 환자 이름과 환자 ID로 데이터베이스에서 환자 엔티티 조회
     * 3. 동일한 회원-환자 조합의 관계가 이미 존재하는지 확인
     * 4. 중복이 없으면 RelationType.CAREGIVER로 MemberPatientRelation 엔티티 생성
     * 5. 생성된 간병인 관계를 데이터베이스에 저장
     * 6. 저장된 엔티티 반환
     *
     * @param memberId 간병인 역할을 할 회원의 ID
     * @param request 환자 이름과 환자 ID를 포함한 생성 요청
     * @return 데이터베이스에 저장된 간병인 관계 엔티티
     * @throws ApplicationException 회원이나 환자를 찾을 수 없는 경우 (NOT_FOUND), 이미 관계가 존재하는 경우 (ALREADY_EXISTS)
     */
    private MemberPatientRelation saveCareGiverWithRequest(Long memberId, CreateCareGiverRelationRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var patient = patientRepository.findByNameAndId(request.name(), request.patientId())
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (memberPatientRelationRepository.existsByMemberAndPatient(member, patient)) {
            throw new ApplicationException(ErrorMessage.ALREADY_EXISTS);
        }
        var careGiverRelation = new MemberPatientRelation(member, patient, RelationType.CAREGIVER);
        return memberPatientRelationRepository.save(careGiverRelation);
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
    private CreateCareGiverRelationResponse createCareGiverRelationResponse(MemberPatientRelation memberPatientRelation) {
        var patient = memberPatientRelation.getPatient();
        return CreateCareGiverRelationResponse.of(memberPatientRelation.getId(), patient.getId(), patient.getName());
    }

    /**
     * 혈당 알림 정책 자동 생성
     * <p>
     * 처리 단계:
     * 1. 생성된 간병인 관계로 GlucoseAlertPolicy 엔티티 생성 (기본값: 고위험 140, 매우 고위험 180)
     * 2. 생성된 정책을 데이터베이스에 저장
     *
     * @param memberPatientRelation 혈당 알림 정책을 생성할 간병인 관계 엔티티
     */
    private void createGlucoseAlertPolicy(MemberPatientRelation memberPatientRelation) {
        var glucoseAlertPolicy = new GlucoseAlertPolicy(memberPatientRelation);
        glucoseAlertPolicyRepository.save(glucoseAlertPolicy);
    }
}
