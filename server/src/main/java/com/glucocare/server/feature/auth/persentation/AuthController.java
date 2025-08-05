package com.glucocare.server.feature.auth.persentation;

import com.glucocare.server.feature.auth.application.*;
import com.glucocare.server.feature.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final ExistsUniqueEmailUseCase existsUniqueEmailUseCase;
    private final AutoLoginUseCase autoLoginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        var auth = registerUseCase.execute(registerRequest);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var auth = loginUseCase.execute(loginRequest);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("/auto-login")
    public ResponseEntity<Boolean> autoLogin(@AuthenticationPrincipal Long memberId) {
        autoLoginUseCase.execute(memberId);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Boolean> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenUseCase.execute(refreshTokenRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/exists-email")
    public ResponseEntity<Boolean> existsUniqueEmail(@Valid @RequestBody ExistsUniqueEmailRequest existsUniqueEmailRequest) {
        var result = existsUniqueEmailUseCase.execute(existsUniqueEmailRequest);
        return ResponseEntity.ok(result);
    }
}
