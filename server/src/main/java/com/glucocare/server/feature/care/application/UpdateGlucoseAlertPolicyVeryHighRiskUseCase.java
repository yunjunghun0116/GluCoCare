package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.dto.UpdateGlucoseAlertPolicyVeryHighRiskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateGlucoseAlertPolicyVeryHighRiskUseCase {
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    public void execute(Long id, Long memberId, UpdateGlucoseAlertPolicyVeryHighRiskRequest request) {
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findById(id)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        glucoseAlertPolicy.getCareRelation()
                          .validateOwnership(memberId);
        glucoseAlertPolicy.updateVeryHighRiskValue(request.veryHighRiskValue());
        glucoseAlertPolicyRepository.save(glucoseAlertPolicy);
    }
}
