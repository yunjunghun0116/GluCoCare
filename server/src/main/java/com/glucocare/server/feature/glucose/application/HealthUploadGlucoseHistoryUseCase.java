package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.feature.glucose.domain.HealthGlucoseHistoryBulkRepository;
import com.glucocare.server.feature.glucose.dto.HealthGlucoseRequest;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HealthUploadGlucoseHistoryUseCase {
    private final HealthGlucoseHistoryBulkRepository healthGlucoseHistoryBulkRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;

    public void execute(Long patientId, List<HealthGlucoseRequest> glucoseRequestList) {
        healthGlucoseHistoryBulkRepository.upsertBatch(patientId, glucoseRequestList);
        glucoseHistoryCache.clearByPatientId(patientId);
    }
}
