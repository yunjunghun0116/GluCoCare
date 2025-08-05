package com.glucocare.server.feature.caregiver.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.caregiver.domain.CareGiver;
import com.glucocare.server.feature.caregiver.domain.CareGiverRepository;
import com.glucocare.server.feature.caregiver.dto.CreateCareGiverRequest;
import com.glucocare.server.feature.caregiver.dto.CreateCareGiverResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 간병인(CareGiver) 생성을 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 새로운 간병인 관계를 생성하는 비즈니스 로직을 처리합니다.
 * 간병인 관계는 회원(Member)과 환자(Patient) 간의 연결을 나타냅니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateCareGiverUseCase {
    private final MemberRepository memberRepository;
    private final PatientRepository patientRepository;
    private final CareGiverRepository careGiverRepository;

    /**
     * 간병인 관계를 생성하는 메인 메서드
     *
     * @param memberId 간병인 역할을 할 회원의 ID
     * @param request  간병인 생성 요청 데이터 (환자 이름, CGM 서버 URL 포함)
     * @return 생성된 간병인 관계의 상세 정보를 담은 응답 객체
     * @throws ApplicationException 회원 또는 환자를 찾을 수 없는 경우
     */
    public CreateCareGiverResponse execute(Long memberId, CreateCareGiverRequest request) {
        var careGiver = saveCareGiverWithRequest(memberId, request);
        return createCareGiverResponse(careGiver);
    }

    /**
     * 간병인 관계를 생성하고 저장하는 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 memberId로 회원 존재 여부 확인
     * 2. 요청에 포함된 이름과 환자 ID로 환자 존재 여부 확인
     * 3. 회원과 환자 간의 간병인 관계 엔티티 생성
     * 4. 생성된 간병인 관계를 데이터베이스에 저장
     *
     * @param memberId 간병인 역할을 할 회원의 ID
     * @param request  환자 정보가 담긴 생성 요청 객체
     * @return 저장된 간병인 관계 엔티티
     * @throws ApplicationException 회원이나 환자를 찾을 수 없는 경우
     */
    private CareGiver saveCareGiverWithRequest(Long memberId, CreateCareGiverRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var patient = patientRepository.findByNameAndId(request.name(), request.patientId())
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (careGiverRepository.existsByMemberAndPatient(member, patient)) {
            throw new ApplicationException(ErrorMessage.ALREADY_EXISTS);
        }
        var careGiver = new CareGiver(member, patient);
        return careGiverRepository.save(careGiver);
    }

    /**
     * 간병인 엔티티를 응답 객체로 변환하는 메서드
     * <p>
     * 이 메서드는 생성된 간병인 관계 엔티티에서 필요한 정보를 추출하여
     * 클라이언트에게 반환할 응답 객체를 생성합니다.
     *
     * @param careGiver 변환할 간병인 관계 엔티티
     * @return 간병인 ID, 환자 ID, 환자 이름을 포함한 응답 객체
     */
    private CreateCareGiverResponse createCareGiverResponse(CareGiver careGiver) {
        var patient = careGiver.getPatient();
        return CreateCareGiverResponse.of(careGiver.getId(), patient.getId(), patient.getName());
    }
}
