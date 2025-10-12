package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import com.glucocare.server.feature.patient.dto.UpdateCgmServerUrlRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CGM 서버 URL 수정 Use Case
 * <p>
 * 환자의 CGM 서버 URL을 수정합니다.
 * CGM 서버 URL은 환자의 혈당 데이터를 조회하는 서버 주소를 나타냅니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCgmServerUrlUseCase {
    private final PatientRepository patientRepository;

    /**
     * CGM 서버 URL 수정
     * <p>
     * 비즈니스 로직 순서:
     * 1. 환자 ID로 데이터베이스에서 환자 엔티티 조회
     * 2. 환자 엔티티의 CGM 서버 URL 업데이트
     * 3. 변경된 환자 정보를 데이터베이스에 저장
     *
     * @param patientId 수정할 환자의 ID
     * @param request   새로운 CGM 서버 URL을 포함한 수정 요청
     * @throws ApplicationException 환자를 찾을 수 없는 경우 (NOT_FOUND)
     */
    public void execute(Long patientId, UpdateCgmServerUrlRequest request) {
        var patient = patientRepository.findById(patientId)
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        patient.updateCgmServerUrl(request.cgmServerUrl());
        patientRepository.save(patient);
    }
}
