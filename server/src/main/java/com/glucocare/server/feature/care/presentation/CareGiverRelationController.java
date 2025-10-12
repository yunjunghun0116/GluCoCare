package com.glucocare.server.feature.care.presentation;

import com.glucocare.server.feature.care.application.CreateCareGiverRelationUseCase;
import com.glucocare.server.feature.care.application.DeleteCareGiverRelationUseCase;
import com.glucocare.server.feature.care.application.ReadAllCareGiverRelationUseCase;
import com.glucocare.server.feature.care.application.ReadCareGiverRelationUseCase;
import com.glucocare.server.feature.care.dto.CreateCareGiverRelationRequest;
import com.glucocare.server.feature.care.dto.CreateCareGiverRelationResponse;
import com.glucocare.server.feature.care.dto.ReadCareGiverRelationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/care-givers")
public class CareGiverRelationController {

    private final CreateCareGiverRelationUseCase createCareGiverRelationUseCase;
    private final ReadCareGiverRelationUseCase readCareGiverRelationUseCase;
    private final ReadAllCareGiverRelationUseCase readAllCareGiverRelationUseCase;
    private final DeleteCareGiverRelationUseCase deleteCareGiverRelationUseCase;

    @PostMapping
    public ResponseEntity<CreateCareGiverRelationResponse> createCareGiver(@AuthenticationPrincipal Long memberId, @Valid @RequestBody CreateCareGiverRelationRequest createCareGiverRelationRequest) {
        var response = createCareGiverRelationUseCase.execute(memberId, createCareGiverRelationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadCareGiverRelationResponse> readCareGiver(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        var response = readCareGiverRelationUseCase.execute(memberId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReadCareGiverRelationResponse>> readCareGivers(@AuthenticationPrincipal Long memberId) {
        var response = readAllCareGiverRelationUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareGiver(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        deleteCareGiverRelationUseCase.execute(memberId, id);
        return ResponseEntity.ok()
                             .build();
    }
}
