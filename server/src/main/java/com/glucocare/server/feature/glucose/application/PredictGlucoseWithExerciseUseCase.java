package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.client.PredictClient;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.glucose.dto.PredictGlucoseResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PredictGlucoseWithExerciseUseCase {
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final CareRelationRepository careRelationRepository;
    private final PredictClient predictClient;

    public List<PredictGlucoseResponse> execute(Long memberId, Long careRelationId, Integer duration) {
        var careRelation = careRelationRepository.findById(careRelationId)
                                                 .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careRelation.validateOwnership(memberId);
        var recentGlucoseHistories = glucoseHistoryRepository.findTop20ByPatientOrderByDateDesc(careRelation.getPatient());
        if (recentGlucoseHistories.size() < 20) {
            throw new ApplicationException(ErrorMessage.NEED_MORE_GLUCOSE_HISTORIES);
        }
        return predictClient.predictExerciseGlucose(recentGlucoseHistories, duration);
    }
}
