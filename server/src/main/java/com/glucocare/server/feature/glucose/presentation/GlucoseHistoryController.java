package com.glucocare.server.feature.glucose.presentation;

import com.glucocare.server.feature.glucose.application.CreateGlucoseHistoryUseCase;
import com.glucocare.server.feature.glucose.application.PredictGlucoseUseCase;
import com.glucocare.server.feature.glucose.application.PredictGlucoseWithExerciseUseCase;
import com.glucocare.server.feature.glucose.application.ReadAllGlucoseHistoryUseCase;
import com.glucocare.server.feature.glucose.dto.CreateGlucoseHistoryRequest;
import com.glucocare.server.feature.glucose.dto.PredictGlucoseResponse;
import com.glucocare.server.feature.glucose.dto.ReadGlucoseHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/glucose-histories")
public class GlucoseHistoryController {

    private final ReadAllGlucoseHistoryUseCase readAllGlucoseHistoryUseCase;
    private final CreateGlucoseHistoryUseCase createGlucoseHistoryUseCase;
    private final PredictGlucoseUseCase predictGlucoseUseCase;
    private final PredictGlucoseWithExerciseUseCase predictGlucoseWithExerciseUseCase;

    @GetMapping
    public ResponseEntity<List<ReadGlucoseHistoryResponse>> readAll(@AuthenticationPrincipal Long memberId, @RequestParam Long careRelationId) {
        var response = readAllGlucoseHistoryUseCase.execute(memberId, careRelationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> create(@AuthenticationPrincipal Long memberId, @RequestBody CreateGlucoseHistoryRequest createGlucoseHistoryRequest) {
        createGlucoseHistoryUseCase.execute(memberId, createGlucoseHistoryRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @GetMapping("/predict")
    public ResponseEntity<List<PredictGlucoseResponse>> predict(@AuthenticationPrincipal Long memberId, @RequestParam Long careRelationId) {
        var response = predictGlucoseUseCase.execute(memberId, careRelationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/predict/exercise")
    public ResponseEntity<List<PredictGlucoseResponse>> predictExercise(@AuthenticationPrincipal Long memberId, @RequestParam Long careRelationId, @RequestParam Integer duration) {
        var response = predictGlucoseWithExerciseUseCase.execute(memberId, careRelationId, duration);
        return ResponseEntity.ok(response);
    }
}
