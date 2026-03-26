package com.glucocare.server.feature.point.presentation;

import com.glucocare.server.feature.point.application.EarnPointUseCase;
import com.glucocare.server.feature.point.application.ReadAllPointHistoryUseCase;
import com.glucocare.server.feature.point.application.ReadPointUseCase;
import com.glucocare.server.feature.point.application.SpendPointUseCase;
import com.glucocare.server.feature.point.dto.EarnPointRequest;
import com.glucocare.server.feature.point.dto.PointHistoryResponse;
import com.glucocare.server.feature.point.dto.PointResponse;
import com.glucocare.server.feature.point.dto.SpendPointRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/points")
public class PointController {

    private final EarnPointUseCase earnPointUseCase;
    private final SpendPointUseCase spendPointUseCase;
    private final ReadPointUseCase readPointUseCase;
    private final ReadAllPointHistoryUseCase readAllPointHistoryUseCase;

    @PostMapping("/earn")
    public ResponseEntity<Void> earnPoint(@AuthenticationPrincipal Long memberId, @Valid @RequestBody EarnPointRequest earnPointRequest) {
        earnPointUseCase.execute(memberId, earnPointRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/spend")
    public ResponseEntity<Void> spendPoint(@AuthenticationPrincipal Long memberId, @Valid @RequestBody SpendPointRequest spendPointRequest) {
        spendPointUseCase.execute(memberId, spendPointRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @GetMapping
    public ResponseEntity<PointResponse> readPoint(@AuthenticationPrincipal Long memberId) {
        var response = readPointUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<PointHistoryResponse>> readHistories(@AuthenticationPrincipal Long memberId) {
        var response = readAllPointHistoryUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }
}
