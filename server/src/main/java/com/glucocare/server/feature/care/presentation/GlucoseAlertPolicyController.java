package com.glucocare.server.feature.care.presentation;

import com.glucocare.server.feature.care.application.ReadGlucoseAlertPolicyUseCase;
import com.glucocare.server.feature.care.application.UpdateGlucoseAlertPolicyHighRiskUseCase;
import com.glucocare.server.feature.care.application.UpdateGlucoseAlertPolicyVeryHighRiskUseCase;
import com.glucocare.server.feature.care.dto.ReadGlucoseAlertPolicyResponse;
import com.glucocare.server.feature.care.dto.UpdateGlucoseAlertPolicyHighRiskRequest;
import com.glucocare.server.feature.care.dto.UpdateGlucoseAlertPolicyVeryHighRiskRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/glucose-alert-policies")
public class GlucoseAlertPolicyController {

    private final UpdateGlucoseAlertPolicyVeryHighRiskUseCase updateGlucoseAlertPolicyVeryHighRiskUseCase;
    private final UpdateGlucoseAlertPolicyHighRiskUseCase updateGlucoseAlertPolicyHighRiskUseCase;
    private final ReadGlucoseAlertPolicyUseCase readGlucoseAlertPolicyUseCase;

    @PostMapping("/{id}/high-risk")
    public ResponseEntity<Void> updateGlucoseAlertPolicyHighRisk(@PathVariable Long id, @Valid @RequestBody UpdateGlucoseAlertPolicyHighRiskRequest updateGlucoseAlertPolicyRequest) {
        updateGlucoseAlertPolicyHighRiskUseCase.execute(id, updateGlucoseAlertPolicyRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/{id}/very-high-risk")
    public ResponseEntity<Void> updateGlucoseAlertPolicyVeryHighRisk(@PathVariable Long id, @Valid @RequestBody UpdateGlucoseAlertPolicyVeryHighRiskRequest updateGlucoseAlertPolicyRequest) {
        updateGlucoseAlertPolicyVeryHighRiskUseCase.execute(id, updateGlucoseAlertPolicyRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @GetMapping
    public ResponseEntity<ReadGlucoseAlertPolicyResponse> readGlucoseAlertPolicy(@AuthenticationPrincipal Long memberId, @RequestParam Long careGiverId) {
        var response = readGlucoseAlertPolicyUseCase.execute(memberId, careGiverId);
        return ResponseEntity.ok(response);
    }
}
