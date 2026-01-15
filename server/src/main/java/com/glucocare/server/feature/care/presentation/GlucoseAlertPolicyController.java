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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/glucose-alert-policies")
public class GlucoseAlertPolicyController {

    private final UpdateGlucoseAlertPolicyVeryHighRiskUseCase updateGlucoseAlertPolicyVeryHighRiskUseCase;
    private final UpdateGlucoseAlertPolicyHighRiskUseCase updateGlucoseAlertPolicyHighRiskUseCase;
    private final ReadGlucoseAlertPolicyUseCase readGlucoseAlertPolicyUseCase;

    @PostMapping("/{id}/high-risk")
    public ResponseEntity<Void> updateGlucoseAlertPolicyHighRisk(@PathVariable Long id, @AuthenticationPrincipal Long memberId, @Valid @RequestBody UpdateGlucoseAlertPolicyHighRiskRequest updateGlucoseAlertPolicyRequest) {
        updateGlucoseAlertPolicyHighRiskUseCase.execute(id, memberId, updateGlucoseAlertPolicyRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/{id}/very-high-risk")
    public ResponseEntity<Void> updateGlucoseAlertPolicyVeryHighRisk(@PathVariable Long id, @AuthenticationPrincipal Long memberId, @Valid @RequestBody UpdateGlucoseAlertPolicyVeryHighRiskRequest updateGlucoseAlertPolicyRequest) {
        updateGlucoseAlertPolicyVeryHighRiskUseCase.execute(id, memberId, updateGlucoseAlertPolicyRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @GetMapping
    public ResponseEntity<ReadGlucoseAlertPolicyResponse> readGlucoseAlertPolicy(@AuthenticationPrincipal Long memberId, @RequestParam Long careRelationId) {
        var response = readGlucoseAlertPolicyUseCase.execute(memberId, careRelationId);
        return ResponseEntity.ok(response);
    }
}
