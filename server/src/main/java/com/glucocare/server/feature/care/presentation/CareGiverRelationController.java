package com.glucocare.server.feature.care.presentation;

import com.glucocare.server.feature.care.application.CreateCareRelationUseCase;
import com.glucocare.server.feature.care.application.DeleteCareRelationUseCase;
import com.glucocare.server.feature.care.application.ReadAllCareRelationUseCase;
import com.glucocare.server.feature.care.application.ReadCareRelationUseCase;
import com.glucocare.server.feature.care.dto.CreateCareRelationRequest;
import com.glucocare.server.feature.care.dto.CreateCareRelationResponse;
import com.glucocare.server.feature.care.dto.ReadCareRelationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/care-givers")
public class CareGiverRelationController {

    private final CreateCareRelationUseCase createCareRelationUseCase;
    private final ReadCareRelationUseCase readCareRelationUseCase;
    private final ReadAllCareRelationUseCase readAllCareRelationUseCase;
    private final DeleteCareRelationUseCase deleteCareRelationUseCase;

    @PostMapping
    public ResponseEntity<CreateCareRelationResponse> createCareGiver(@AuthenticationPrincipal Long memberId, @Valid @RequestBody CreateCareRelationRequest createCareRelationRequest) {
        var response = createCareRelationUseCase.execute(memberId, createCareRelationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadCareRelationResponse> readCareGiver(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        var response = readCareRelationUseCase.execute(memberId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReadCareRelationResponse>> readCareGivers(@AuthenticationPrincipal Long memberId) {
        var response = readAllCareRelationUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareGiver(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        deleteCareRelationUseCase.execute(memberId, id);
        return ResponseEntity.ok()
                             .build();
    }
}
