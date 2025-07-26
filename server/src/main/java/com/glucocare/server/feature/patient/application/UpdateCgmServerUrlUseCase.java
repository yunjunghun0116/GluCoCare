package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import com.glucocare.server.feature.patient.dto.UpdateCgmServerUrlRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCgmServerUrlUseCase {
    private final PatientRepository patientRepository;

    @Transactional
    public void execute(Long patientId, UpdateCgmServerUrlRequest request) {
        var patient = patientRepository.findById(patientId)
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        patient.updateCgmServerUrl(request.cgmServerUrl());
        patientRepository.save(patient);
    }
}
