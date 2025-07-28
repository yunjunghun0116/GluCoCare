package com.glucocare.server.feature.member.persentation;

import com.glucocare.server.feature.member.application.LoginUseCase;
import com.glucocare.server.feature.member.application.RegisterUseCase;
import com.glucocare.server.feature.member.application.UpdateNameUseCase;
import com.glucocare.server.feature.member.dto.AuthResponse;
import com.glucocare.server.feature.member.dto.LoginRequest;
import com.glucocare.server.feature.member.dto.RegisterRequest;
import com.glucocare.server.feature.member.dto.UpdateNameRequest;
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
public class MemberController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final UpdateNameUseCase updateNameUseCase;

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

    @PostMapping("/update-name")
    public ResponseEntity<AuthResponse> updateName(@AuthenticationPrincipal Long memberId, @Valid @RequestBody UpdateNameRequest updateNameRequest) {
        updateNameUseCase.execute(memberId, updateNameRequest);
        return ResponseEntity.ok()
                             .build();
    }
}
