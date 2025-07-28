package com.glucocare.server.feature.glucosehistory.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.caregiver.domain.CareGiverRepository;
import com.glucocare.server.feature.glucosehistory.domain.GlucoseHistory;
import com.glucocare.server.feature.glucosehistory.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.glucosehistory.dto.ReadGlucoseHistoryResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllGlucoseHistoryUseCase {
    private final PatientRepository patientRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final CareGiverRepository careGiverRepository;
    private final MemberRepository memberRepository;

    public List<ReadGlucoseHistoryResponse> execute(Long memberId, Long patientId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var patient = patientRepository.findById(patientId)
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!careGiverRepository.existsByMemberAndPatient(member, patient)) {
            throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        }
        return glucoseHistoryRepository.findAllByPatientOrderByDateDesc(patient)
                                       .stream()
                                       .map(this::convertGlucoseHistoryResponse)
                                       .toList();
    }

    private ReadGlucoseHistoryResponse convertGlucoseHistoryResponse(GlucoseHistory glucoseHistory) {
        return ReadGlucoseHistoryResponse.of(glucoseHistory.getId(), glucoseHistory.getDate(), glucoseHistory.getSgv());
    }
}
