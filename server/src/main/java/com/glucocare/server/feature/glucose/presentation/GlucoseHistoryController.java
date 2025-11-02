package com.glucocare.server.feature.glucose.presentation;

import com.glucocare.server.feature.glucose.application.CreateGlucoseHistoryUseCase;
import com.glucocare.server.feature.glucose.application.ReadAllGlucoseHistoryUseCase;
import com.glucocare.server.feature.glucose.application.ReadRecentGlucoseSyncDateUseCase;
import com.glucocare.server.feature.glucose.dto.CreateGlucoseHistoryRequest;
import com.glucocare.server.feature.glucose.dto.ReadGlucoseHistoryResponse;
import com.glucocare.server.feature.glucose.dto.ReadGlucoseSyncDateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/glucose-histories")
public class GlucoseHistoryController {

    private final ReadAllGlucoseHistoryUseCase readAllGlucoseHistoryUseCase;
    private final CreateGlucoseHistoryUseCase createGlucoseHistoryUseCase;
    private final ReadRecentGlucoseSyncDateUseCase readRecentGlucoseSyncDateUseCase;

    @GetMapping("/{patientId}")
    public ResponseEntity<List<ReadGlucoseHistoryResponse>> readAll(@AuthenticationPrincipal Long memberId, @PathVariable Long patientId) {
        var response = readAllGlucoseHistoryUseCase.execute(memberId, patientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{patientId}/last-sync-date")
    public ResponseEntity<ReadGlucoseSyncDateResponse> readLastSyncDate(@PathVariable Long patientId) {
        var response = readRecentGlucoseSyncDateUseCase.execute(patientId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> create(@AuthenticationPrincipal Long memberId, @RequestBody CreateGlucoseHistoryRequest createGlucoseHistoryRequest) {
        createGlucoseHistoryUseCase.execute(memberId, createGlucoseHistoryRequest);
        return ResponseEntity.ok()
                             .build();
    }
}
