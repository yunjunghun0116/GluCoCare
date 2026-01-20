package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.glucose.dto.CreateGlucoseHistoryRequest;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateGlucoseHistoryUseCase {

    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final CareRelationRepository careRelationRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;

    public void execute(Long memberId, CreateGlucoseHistoryRequest request) {
        var careRelation = careRelationRepository.findById(request.careRelationId())
                                                 .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careRelation.validateOwnership(memberId);
        var patient = careRelation.getPatient();
        var glucoseHistory = new GlucoseHistory(patient, request.sgv(), request.dateTime());
        glucoseHistoryRepository.save(glucoseHistory);
        glucoseHistoryCache.clearByPatientId(patient.getId());
    }
}
