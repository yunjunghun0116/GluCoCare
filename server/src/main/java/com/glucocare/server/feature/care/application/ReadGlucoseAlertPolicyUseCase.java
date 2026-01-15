package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicy;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.dto.ReadGlucoseAlertPolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadGlucoseAlertPolicyUseCase {
    private final CareRelationRepository careRelationRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    public ReadGlucoseAlertPolicyResponse execute(Long memberId, Long relationId) {
        var careRelation = careRelationRepository.findById(relationId)
                                                 .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careRelation.validateOwnership(memberId);
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findByCareRelation(careRelation);
        return glucoseAlertPolicy.map(this::convertGlucoseAlertPolicyResponse)
                                 .orElseGet(() -> convertGlucoseAlertPolicyResponse(createGlucoseAlertPolicy(careRelation)));
    }

    private ReadGlucoseAlertPolicyResponse convertGlucoseAlertPolicyResponse(GlucoseAlertPolicy glucoseAlertPolicy) {
        var careRelation = glucoseAlertPolicy.getCareRelation();
        return ReadGlucoseAlertPolicyResponse.of(glucoseAlertPolicy.getId(), careRelation.getId(), glucoseAlertPolicy.getHighRiskValue(), glucoseAlertPolicy.getVeryHighRiskValue());
    }

    private GlucoseAlertPolicy createGlucoseAlertPolicy(CareRelation careRelation) {
        var glucoseAlertPolicy = new GlucoseAlertPolicy(careRelation);
        return glucoseAlertPolicyRepository.save(glucoseAlertPolicy);
    }
}
