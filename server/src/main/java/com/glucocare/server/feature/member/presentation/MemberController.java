package com.glucocare.server.feature.member.presentation;

import com.glucocare.server.feature.member.application.DeleteMemberUseCase;
import com.glucocare.server.feature.member.application.ReadMemberNameUseCase;
import com.glucocare.server.feature.member.application.UpdateMemberToPatientUseCase;
import com.glucocare.server.feature.member.application.UpdateNameUseCase;
import com.glucocare.server.feature.member.dto.UpdateMemberNameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final UpdateNameUseCase updateNameUseCase;
    private final ReadMemberNameUseCase readMemberNameUseCase;
    private final UpdateMemberToPatientUseCase updateMemberToPatientUseCase;
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

    @PostMapping("/update-to-patient")
    public ResponseEntity<Void> updateToPatient(@AuthenticationPrincipal Long memberId) {
        updateMemberToPatientUseCase.execute(memberId);
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
