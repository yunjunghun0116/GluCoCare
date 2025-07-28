package com.glucocare.server.feature.caregiver.presentation;

import com.glucocare.server.feature.caregiver.application.CreateCareGiverUseCase;
import com.glucocare.server.feature.caregiver.application.DeleteCareGiverUseCase;
import com.glucocare.server.feature.caregiver.application.ReadAllCareGiverUseCase;
import com.glucocare.server.feature.caregiver.dto.CreateCareGiverRequest;
import com.glucocare.server.feature.caregiver.dto.CreateCareGiverResponse;
import com.glucocare.server.feature.caregiver.dto.ReadCareGiverResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/care-givers")
public class CareGiverController {

    private final CreateCareGiverUseCase createCareGiverUseCase;
    private final ReadAllCareGiverUseCase readAllCareGiverUseCase;
    private final DeleteCareGiverUseCase deleteCareGiverUseCase;

    @PostMapping
    public ResponseEntity<CreateCareGiverResponse> createCareGiver(@AuthenticationPrincipal Long memberId, @Valid @RequestBody CreateCareGiverRequest createCareGiverRequest) {
        var response = createCareGiverUseCase.execute(memberId, createCareGiverRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReadCareGiverResponse>> readCareGivers(@AuthenticationPrincipal Long memberId) {
        var response = readAllCareGiverUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareGiver(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        deleteCareGiverUseCase.execute(memberId, id);
        return ResponseEntity.ok()
                             .build();
    }
}
