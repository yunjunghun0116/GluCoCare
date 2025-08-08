package com.glucocare.server.feature.patient.application;

import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import com.glucocare.server.feature.patient.dto.CreatePatientRequest;
import com.glucocare.server.feature.patient.dto.CreatePatientResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 환자(Patient) 생성 기능을 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 새로운 환자를 등록하는 비즈니스 로직을 처리합니다.
 * 환자는 이름과 CGM(Continuous Glucose Monitoring) 서버 URL을 가지며,
 * 이를 통해 혈당 데이터를 모니터링할 수 있습니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreatePatientUseCase {
    private final PatientRepository patientRepository;

    /**
     * 새로운 환자를 생성하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 요청 정보로 새로운 환자 엔티티 생성 및 저장
     * 2. 생성된 환자 정보를 응답 객체로 변환하여 반환
     *
     * @param request 환자 생성 요청 정보 (이름, CGM 서버 URL 포함)
     * @return 생성된 환자의 상세 정보를 담은 응답 객체
     */
    public CreatePatientResponse execute(CreatePatientRequest request) {
        var patient = savePatientWithRequest(request);
        return CreatePatientResponse.of(patient.getId(), patient.getName(), patient.getCgmServerUrl());
    }

    /**
     * 생성 요청 정보로 환자 엔티티를 생성하고 저장하는 메서드
     * <p>
     * 요청된 환자 이름과 CGM 서버 URL로 새로운 환자 엔티티를 생성하고
     * 데이터베이스에 영구 저장합니다.
     *
     * @param request 환자 생성에 필요한 정보를 담은 요청 객체
     * @return 생성되고 저장된 환자 엔티티
     */
    private Patient savePatientWithRequest(CreatePatientRequest request) {
        var patient = new Patient(request.name(), request.cgmServerUrl());
        return patientRepository.save(patient);
    }
}
