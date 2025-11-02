package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.glucose.dto.CreateGlucoseHistoryRequest;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateGlucoseHistoryUseCase {

    private final PatientRepository patientRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;
    private final ZoneOffset zoneOffset = ZoneOffset.UTC;

    public void execute(Long patientId, CreateGlucoseHistoryRequest request) {
        var patient = patientRepository.findById(patientId)
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var milliseconds = request.dateTime()
                                  .toInstant(zoneOffset)
                                  .toEpochMilli();
        var glucoseHistory = new GlucoseHistory(patient, request.value(), milliseconds);
        glucoseHistoryRepository.save(glucoseHistory);

        glucoseHistoryCache.clearByPatientId(patientId);
    }
}
