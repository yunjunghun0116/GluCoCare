package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareGiverRepository;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicy;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.dto.ReadGlucoseAlertPolicyResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadGlucoseAlertPolicyUseCase {
    private final CareGiverRepository careGiverRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    public ReadGlucoseAlertPolicyResponse execute(Long memberId, Long careGiverId) {
        var careGiver = careGiverRepository.findById(careGiverId)
                                           .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!careGiver.getMember()
                      .getId()
                      .equals(memberId)) throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findByCareGiver(careGiver)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return convertGlucoseAlertPolicyResponse(glucoseAlertPolicy);
    }

    private ReadGlucoseAlertPolicyResponse convertGlucoseAlertPolicyResponse(GlucoseAlertPolicy glucoseAlertPolicy) {
        var careGiver = glucoseAlertPolicy.getCareGiver();
        return ReadGlucoseAlertPolicyResponse.of(glucoseAlertPolicy.getId(), careGiver.getId(), glucoseAlertPolicy.getHighRiskValue(), glucoseAlertPolicy.getVeryHighRiskValue());
    }
}
