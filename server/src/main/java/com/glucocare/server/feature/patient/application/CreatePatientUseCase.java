package com.glucocare.server.feature.patient.application;

import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import com.glucocare.server.feature.patient.dto.CreatePatientRequest;
import com.glucocare.server.feature.patient.dto.CreatePatientResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePatientUseCase {
    private final PatientRepository patientRepository;

    @Transactional
    public CreatePatientResponse execute(CreatePatientRequest request) {
        var patient = savePatientWithRequest(request);
        return CreatePatientResponse.of(patient.getId(), patient.getName(), patient.getCgmServerUrl());
    }

    private Patient savePatientWithRequest(CreatePatientRequest request) {
        var patient = new Patient(request.name(), request.cgmServerUrl());
        return patientRepository.save(patient);
    }
}
