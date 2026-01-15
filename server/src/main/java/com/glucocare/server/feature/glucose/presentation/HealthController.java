package com.glucocare.server.feature.glucose.presentation;

import com.glucocare.server.feature.glucose.application.HealthUploadGlucoseHistoryUseCase;
import com.glucocare.server.feature.glucose.dto.HealthGlucoseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/health")
public class HealthController {
    private final HealthUploadGlucoseHistoryUseCase createGlucoseHistoryUseCase;

    @PostMapping
    public ResponseEntity<Void> create(@AuthenticationPrincipal Long memberId, @RequestBody List<HealthGlucoseRequest> glucoseRequestList) {
        createGlucoseHistoryUseCase.execute(memberId, glucoseRequestList);
        return ResponseEntity.ok()
                             .build();
    }
}
