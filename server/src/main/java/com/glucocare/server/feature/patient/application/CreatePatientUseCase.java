package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.domain.RelationType;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import com.glucocare.server.feature.patient.dto.CreatePatientRequest;
import com.glucocare.server.feature.patient.dto.CreatePatientResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 환자 생성 Use Case
 * <p>
 * 새로운 환자를 등록하고 회원-환자 관계(PATIENT 타입)를 생성합니다.
 * 환자는 이름과 CGM 서버 URL을 가지며, 이를 통해 혈당 데이터를 모니터링할 수 있습니다.
 * 한 회원은 하나의 환자 관계만 가질 수 있습니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreatePatientUseCase {
    private final MemberRepository memberRepository;
    private final PatientRepository patientRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;
    @Value("${app.cgm-server.url-format}")
    private String cgmServerUrlFormat;

    /**
     * 환자 생성 및 회원-환자 관계 등록
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 해당 회원이 이미 환자 관계(PATIENT 타입)를 가지고 있는지 확인
     * 3. 이미 환자 관계가 있으면 예외 발생 (한 회원당 하나의 환자만 등록 가능)
     * 4. 환자 이름으로 환자 엔티티 생성 및 저장
     * 5. 저장된 환자 ID를 사용하여 CGM 서버 URL 생성 및 업데이트
     * 6. 회원-환자 관계(PATIENT 타입) 생성 및 저장
     * 7. 생성된 환자 정보를 응답 DTO로 반환
     *
     * @param memberId 환자를 등록할 회원의 ID
     * @param request  환자 이름을 포함한 생성 요청
     * @return 환자 ID, 환자 이름, CGM 서버 URL을 포함한 응답
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND), 이미 환자가 등록된 경우 (ALREADY_EXISTS_PATIENT)
     */
    public CreatePatientResponse execute(Long memberId, CreatePatientRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (memberPatientRelationRepository.existsByMemberAndRelationType(member, RelationType.PATIENT)) {
            throw new ApplicationException(ErrorMessage.ALREADY_EXISTS_PATIENT);
        }
        var patient = savePatientWithRequest(request);
        savePatientRelation(member, patient);
        return CreatePatientResponse.of(patient.getId(), patient.getName(), patient.getCgmServerUrl());
    }

    /**
     * 환자 엔티티 생성 및 CGM 서버 URL 설정
     * <p>
     * 처리 단계:
     * 1. 환자 이름으로 환자 엔티티 생성
     * 2. 환자 엔티티를 데이터베이스에 저장 (ID 자동 생성)
     * 3. 생성된 환자 ID를 포함한 CGM 서버 URL 생성
     * 4. 환자 엔티티에 CGM 서버 URL 업데이트 (Dirty Checking으로 자동 저장)
     * 5. 업데이트된 환자 엔티티 반환
     *
     * @param request 환자 이름을 포함한 생성 요청
     * @return CGM 서버 URL이 설정된 환자 엔티티
     */
    private Patient savePatientWithRequest(CreatePatientRequest request) {
        var patient = new Patient(request.name());
        var savedPatient = patientRepository.save(patient);

        var cgmServerUrl = String.format(cgmServerUrlFormat, savedPatient.getId());
        savedPatient.updateCgmServerUrl(cgmServerUrl);
        return savedPatient;
    }

    /**
     * 회원-환자 관계 생성 및 저장
     * <p>
     * 처리 단계:
     * 1. 회원과 환자 엔티티로 MemberPatientRelation 생성 (RelationType.PATIENT)
     * 2. 생성된 관계를 데이터베이스에 저장
     *
     * @param member  관계를 생성할 회원 엔티티
     * @param patient 관계를 생성할 환자 엔티티
     */
    private void savePatientRelation(Member member, Patient patient) {
        var patientRelation = new MemberPatientRelation(member, patient, RelationType.PATIENT);
        memberPatientRelationRepository.save(patientRelation);
    }
}
