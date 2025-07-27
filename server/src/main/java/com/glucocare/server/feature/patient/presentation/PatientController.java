package com.glucocare.server.feature.patient.presentation;

import com.glucocare.server.feature.patient.application.CreatePatientUseCase;
import com.glucocare.server.feature.patient.application.UpdateCgmServerUrlUseCase;
import com.glucocare.server.feature.patient.dto.CreatePatientRequest;
import com.glucocare.server.feature.patient.dto.CreatePatientResponse;
import com.glucocare.server.feature.patient.dto.UpdateCgmServerUrlRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;
    private final UpdateCgmServerUrlUseCase updateCgmServerUrlUseCase;

    @PostMapping
    public ResponseEntity<CreatePatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest createPatientRequest) {
        var response = createPatientUseCase.execute(createPatientRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{patientId}")
    public ResponseEntity<Void> updateCgmServerUrl(@PathVariable Long patientId, @Valid @RequestBody UpdateCgmServerUrlRequest updateCgmServerUrlRequest) {
        updateCgmServerUrlUseCase.execute(patientId, updateCgmServerUrlRequest);
        return ResponseEntity.ok()
                             .build();
    }
}
