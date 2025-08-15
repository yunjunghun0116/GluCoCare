package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.dto.UpdateGlucoseAlertPolicyVeryHighRiskRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateGlucoseAlertPolicyVeryHighUseCase {
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    public void execute(Long id, Long memberId, UpdateGlucoseAlertPolicyVeryHighRiskRequest request) {
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findById(id)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!glucoseAlertPolicy.getCareGiver()
                               .getMember()
                               .getId()
                               .equals(memberId)) {
            throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        }
        glucoseAlertPolicy.updateVeryHighRiskValue(request.veryHighRiskValue());
        glucoseAlertPolicyRepository.save(glucoseAlertPolicy);
    }
}
