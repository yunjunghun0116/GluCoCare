package com.glucocare.server.feature.care.presentation;

import com.glucocare.server.feature.care.application.CreateCareGiverUseCase;
import com.glucocare.server.feature.care.application.DeleteCareGiverUseCase;
import com.glucocare.server.feature.care.application.ReadAllCareGiverUseCase;
import com.glucocare.server.feature.care.application.ReadCareGiverUseCase;
import com.glucocare.server.feature.care.dto.CreateCareGiverRequest;
import com.glucocare.server.feature.care.dto.CreateCareGiverResponse;
import com.glucocare.server.feature.care.dto.ReadCareGiverResponse;
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
    private final ReadCareGiverUseCase readCareGiverUseCase;
    private final ReadAllCareGiverUseCase readAllCareGiverUseCase;
    private final DeleteCareGiverUseCase deleteCareGiverUseCase;

    @PostMapping
    public ResponseEntity<CreateCareGiverResponse> createCareGiver(@AuthenticationPrincipal Long memberId, @Valid @RequestBody CreateCareGiverRequest createCareGiverRequest) {
        var response = createCareGiverUseCase.execute(memberId, createCareGiverRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadCareGiverResponse> readCareGiver(@PathVariable Long id) {
        var response = readCareGiverUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReadCareGiverResponse>> readCareGivers(@AuthenticationPrincipal Long memberId) {
        var response = readAllCareGiverUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareGiver(@PathVariable Long id) {
        deleteCareGiverUseCase.execute(id);
        return ResponseEntity.ok()
                             .build();
    }
}
