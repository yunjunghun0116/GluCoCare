package com.glucocare.server.feature.member.persentation;

import com.glucocare.server.feature.member.application.DeleteMemberUseCase;
import com.glucocare.server.feature.member.application.ReadMemberNameUseCase;
import com.glucocare.server.feature.member.application.UpdateNameUseCase;
import com.glucocare.server.feature.member.dto.UpdateMemberNameRequest;
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
    private final DeleteMemberUseCase deleteMemberUseCase;

    @GetMapping("/name")
    public ResponseEntity<String> readName(@AuthenticationPrincipal Long memberId) {
        var name = readMemberNameUseCase.execute(memberId);
        return ResponseEntity.ok(name);
    }

    @PostMapping("/update-name")
    public ResponseEntity<Void> updateName(@AuthenticationPrincipal Long memberId, @Valid @RequestBody UpdateMemberNameRequest updateMemberNameRequest) {
        updateNameUseCase.execute(memberId, updateMemberNameRequest);
        return ResponseEntity.ok()
                             .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long memberId) {
        deleteMemberUseCase.execute(memberId);
        return ResponseEntity.ok()
                             .build();
    }
}
