package com.glucocare.server.feature.member.persentation;

import com.glucocare.server.feature.member.application.ReadMemberNameUseCase;
import com.glucocare.server.feature.member.application.UpdateNameUseCase;
import com.glucocare.server.feature.member.dto.UpdateNameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final UpdateNameUseCase updateNameUseCase;
    private final ReadMemberNameUseCase readMemberNameUseCase;

    @GetMapping("/name")
    public ResponseEntity<String> readName(@AuthenticationPrincipal Long memberId) {
        var name = readMemberNameUseCase.execute(memberId);
        return ResponseEntity.ok(name);
    }

    @PostMapping("/update-name")
    public ResponseEntity<Void> updateName(@AuthenticationPrincipal Long memberId, @Valid @RequestBody UpdateNameRequest updateNameRequest) {
        updateNameUseCase.execute(memberId, updateNameRequest);
        return ResponseEntity.ok()
                             .build();
    }
}
