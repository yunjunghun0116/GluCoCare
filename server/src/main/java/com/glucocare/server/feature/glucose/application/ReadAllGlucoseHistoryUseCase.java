package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.glucose.dto.ReadGlucoseHistoryResponse;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadAllGlucoseHistoryUseCase {
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final CareRelationRepository careRelationRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;

    public List<ReadGlucoseHistoryResponse> execute(Long memberId, Long careRelationId) {
        var careRelation = careRelationRepository.findById(careRelationId)
                                                 .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careRelation.validateOwnership(memberId);
        var patient = careRelation.getPatient();
        if (glucoseHistoryCache.existsByPatient(patient)) {
            return glucoseHistoryCache.readAllByPatient(patient);
        }

        var result = glucoseHistoryRepository.findAllByPatientOrderByDateDesc(patient)
                                             .stream()
                                             .map(this::convertGlucoseHistoryResponse)
                                             .toList();
        glucoseHistoryCache.createGlucoseHistories(patient, result);
        return result;
    }

    private ReadGlucoseHistoryResponse convertGlucoseHistoryResponse(GlucoseHistory glucoseHistory) {
        return ReadGlucoseHistoryResponse.of(glucoseHistory.getId(), glucoseHistory.getDate(), glucoseHistory.getSgv());
    }
}
