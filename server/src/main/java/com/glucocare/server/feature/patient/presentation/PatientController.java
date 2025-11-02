package com.glucocare.server.feature.patient.presentation;

import com.glucocare.server.feature.patient.application.CreatePatientUseCase;
import com.glucocare.server.feature.patient.application.ExistsPatientUseCase;
import com.glucocare.server.feature.patient.application.ReadPatientUseCase;
import com.glucocare.server.feature.patient.application.UpdateCgmServerUrlUseCase;
import com.glucocare.server.feature.patient.dto.CreatePatientRequest;
import com.glucocare.server.feature.patient.dto.CreatePatientResponse;
import com.glucocare.server.feature.patient.dto.ReadPatientResponse;
import com.glucocare.server.feature.patient.dto.UpdateCgmServerUrlRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;
    private final ExistsPatientUseCase existsPatientUseCase;
    private final ReadPatientUseCase readPatientUseCase;
    private final UpdateCgmServerUrlUseCase updateCgmServerUrlUseCase;

    @PostMapping
    public ResponseEntity<CreatePatientResponse> create(@AuthenticationPrincipal Long memberId, @Valid @RequestBody CreatePatientRequest createPatientRequest) {
        var response = createPatientUseCase.execute(memberId, createPatientRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsPatientWithMember(@AuthenticationPrincipal Long memberId) {
        var response = existsPatientUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ReadPatientResponse> read(@AuthenticationPrincipal Long memberId) {
        var response = readPatientUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{patientId}/update-cgm-server-url")
    public ResponseEntity<Void> updateCgmServerUrl(@PathVariable Long patientId, @Valid @RequestBody UpdateCgmServerUrlRequest updateCgmServerUrlRequest) {
        updateCgmServerUrlUseCase.execute(patientId, updateCgmServerUrlRequest);
        return ResponseEntity.ok()
                             .build();
    }
}
