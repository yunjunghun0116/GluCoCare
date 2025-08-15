package com.glucocare.server.feature.notification.presentation;

import com.glucocare.server.feature.notification.application.CreateFCMTokenUseCase;
import com.glucocare.server.feature.notification.dto.CreateFCMTokenRequest;
import com.glucocare.server.feature.notification.dto.CreateFCMTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fcm-tokens")
public class FCMTokenController {

    private final CreateFCMTokenUseCase createFCMTokenUseCase;

    @PostMapping
    public ResponseEntity<CreateFCMTokenResponse> createFCMToken(@AuthenticationPrincipal Long memberId, @Valid @RequestBody CreateFCMTokenRequest createFCMTokenRequest) {
        var response = createFCMTokenUseCase.execute(memberId, createFCMTokenRequest);
        return ResponseEntity.ok(response);
    }
}
