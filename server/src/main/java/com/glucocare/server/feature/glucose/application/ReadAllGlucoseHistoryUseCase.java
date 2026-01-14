package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.glucose.dto.ReadGlucoseHistoryResponse;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllGlucoseHistoryUseCase {
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final CareRelationRepository careRelationRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;

    public List<ReadGlucoseHistoryResponse> execute(Long memberId, Long patientId) {
        validateCareGiver(memberId, patientId);
        if (glucoseHistoryCache.existsByPatientId(patientId)) {
            return glucoseHistoryCache.readAllByPatientId(patientId);
        }

        var result = glucoseHistoryRepository.findAllByPatientIdOrderByDateDesc(patientId)
                                             .stream()
                                             .map(this::convertGlucoseHistoryResponse)
                                             .toList();
        glucoseHistoryCache.createGlucoseHistories(patientId, result);
        return result;
    }

    private ReadGlucoseHistoryResponse convertGlucoseHistoryResponse(GlucoseHistory glucoseHistory) {
        return ReadGlucoseHistoryResponse.of(glucoseHistory.getId(), glucoseHistory.getDate(), glucoseHistory.getSgv());
    }

    private void validateCareGiver(Long memberId, Long patientId) {
        if (!careRelationRepository.existsByMemberIdAndPatientId(memberId, patientId)) {
            throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        }
    }
}
