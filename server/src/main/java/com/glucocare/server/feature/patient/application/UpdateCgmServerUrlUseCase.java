package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import com.glucocare.server.feature.patient.dto.UpdateCgmServerUrlRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 환자의 CGM 서버 URL 업데이트 기능을 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 기존 환자의 CGM(Continuous Glucose Monitoring) 서버 URL을 업데이트하는
 * 비즈니스 로직을 처리합니다. CGM 서버 URL은 환자의 혈당 데이터를 모니터링하는
 * 서버의 주소를 나타냅니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCgmServerUrlUseCase {
    private final PatientRepository patientRepository;

    /**
     * 환자의 CGM 서버 URL을 업데이트하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 patientId로 환자 조회
     * 2. 환자 엔티티의 CGM 서버 URL 업데이트 메서드 호출
     * 3. 변경된 환자 정보를 데이터베이스에 저장
     *
     * @param patientId CGM 서버 URL을 업데이트할 환자의 ID
     * @param request   새로운 CGM 서버 URL 정보를 포함한 업데이트 요청 객체
     * @throws ApplicationException 환자를 찾을 수 없는 경우
     */
    public void execute(Long patientId, UpdateCgmServerUrlRequest request) {
        var patient = patientRepository.findById(patientId)
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        patient.updateCgmServerUrl(request.cgmServerUrl());
        patientRepository.save(patient);
    }
}
