package com.glucocare.server.feature.glucosehistory.presentation;

import com.glucocare.server.feature.glucosehistory.application.ReadAllGlucoseHistoryUseCase;
import com.glucocare.server.feature.glucosehistory.dto.ReadGlucoseHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/patients/{patientId}/glucose-histories")
public class GlucoseHistoryController {

    private final ReadAllGlucoseHistoryUseCase readAllGlucoseHistoryUseCase;

    @GetMapping
    public ResponseEntity<List<ReadGlucoseHistoryResponse>> readGlucoseHistories(@AuthenticationPrincipal Long memberId, @PathVariable Long patientId) {
        var response = readAllGlucoseHistoryUseCase.execute(memberId, patientId);
        return ResponseEntity.ok(response);
    }
}
