package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.glucose.domain.GlucoseSyncDateRepository;
import com.glucocare.server.feature.glucose.dto.ReadGlucoseSyncDateResponse;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadRecentGlucoseSyncDateUseCase {
    private static final LocalDate DEFAULT_SYNC_START_DATE = LocalDate.of(2025, 1, 1);
    private final GlucoseSyncDateRepository glucoseSyncDateRepository;
    private final PatientRepository patientRepository;

    public ReadGlucoseSyncDateResponse execute(Long patientId) {
        var patient = patientRepository.findById(patientId)
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var lastGlucoseSyncDate = glucoseSyncDateRepository.findFirstByPatientOrderByDateDesc(patient);

        return lastGlucoseSyncDate.map(glucoseSyncDate -> ReadGlucoseSyncDateResponse.of(patient.getId(), patient.getName(), glucoseSyncDate.getDate()))
                                  .orElseGet(() -> ReadGlucoseSyncDateResponse.of(patient.getId(), patient.getName(), DEFAULT_SYNC_START_DATE));
    }
}
