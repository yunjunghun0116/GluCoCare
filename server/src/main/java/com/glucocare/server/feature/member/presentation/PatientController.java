package com.glucocare.server.feature.member.presentation;

import com.glucocare.server.feature.member.application.ReadMemberIsPatientUseCase;
import com.glucocare.server.feature.member.application.ReadPatientInformationUseCase;
import com.glucocare.server.feature.member.application.UpdateMemberToPatientUseCase;
import com.glucocare.server.feature.member.dto.PatientInformationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final ReadPatientInformationUseCase readPatientInformationUseCase;
    private final ReadMemberIsPatientUseCase readMemberIsPatientUseCase;
    private final UpdateMemberToPatientUseCase updateMemberToPatientUseCase;

    @GetMapping
    public ResponseEntity<PatientInformationResponse> readPatientInformation(@AuthenticationPrincipal Long memberId) {
        var response = readPatientInformationUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/is-patient")
    public ResponseEntity<Boolean> readIsPatient(@AuthenticationPrincipal Long memberId) {
        var isPatient = readMemberIsPatientUseCase.execute(memberId);
        return ResponseEntity.ok(isPatient);
    }

    @PostMapping
    public ResponseEntity<Void> updateToPatient(@AuthenticationPrincipal Long memberId) {
        updateMemberToPatientUseCase.execute(memberId);
        return ResponseEntity.ok()
                             .build();
    }

}
