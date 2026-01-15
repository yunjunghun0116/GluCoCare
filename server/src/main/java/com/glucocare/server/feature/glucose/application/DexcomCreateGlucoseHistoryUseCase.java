package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.feature.glucose.domain.DexcomGlucoseHistoryBulkRepository;
import com.glucocare.server.feature.glucose.dto.DexcomGlucoseRequest;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DexcomCreateGlucoseHistoryUseCase {
    private final DexcomGlucoseHistoryBulkRepository dexcomGlucoseHistoryBulkRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;

    public void execute(Long patientId, List<DexcomGlucoseRequest> glucoseRequestList) {
        dexcomGlucoseHistoryBulkRepository.upsertBatch(patientId, glucoseRequestList);
        glucoseHistoryCache.clearByPatientId(patientId);
    }
}
