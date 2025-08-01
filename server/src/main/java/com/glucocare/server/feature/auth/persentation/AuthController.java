package com.glucocare.server.feature.auth.persentation;

import com.glucocare.server.feature.auth.application.AutoLoginUseCase;
import com.glucocare.server.feature.auth.application.ExistsUniqueEmailUseCase;
import com.glucocare.server.feature.auth.application.LoginUseCase;
import com.glucocare.server.feature.auth.application.RegisterUseCase;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.auth.dto.ExistsUniqueEmailRequest;
import com.glucocare.server.feature.auth.dto.LoginRequest;
import com.glucocare.server.feature.auth.dto.RegisterRequest;
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
@RequestMapping("/api/members")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final ExistsUniqueEmailUseCase existsUniqueEmailUseCase;
    private final AutoLoginUseCase autoLoginUseCase;

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

    @PostMapping("/auto-login")
    public ResponseEntity<AuthResponse> login(@AuthenticationPrincipal Long memberId) {
        var auth = autoLoginUseCase.execute(memberId);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/exists-email")
    public ResponseEntity<Boolean> existsUniqueEmail(@Valid @RequestBody ExistsUniqueEmailRequest existsUniqueEmailRequest) {
        var result = existsUniqueEmailUseCase.execute(existsUniqueEmailRequest);
        return ResponseEntity.ok(result);
    }
}
