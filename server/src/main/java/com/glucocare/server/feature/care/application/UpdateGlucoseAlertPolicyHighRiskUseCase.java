package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.dto.UpdateGlucoseAlertPolicyHighRiskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateGlucoseAlertPolicyHighRiskUseCase {
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    public void execute(Long id, Long memberId, UpdateGlucoseAlertPolicyHighRiskRequest request) {
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findById(id)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        glucoseAlertPolicy.getCareRelation()
                          .validateOwnership(memberId);
        glucoseAlertPolicy.updateHighRiskValue(request.highRiskValue());
        glucoseAlertPolicyRepository.save(glucoseAlertPolicy);
    }
}
