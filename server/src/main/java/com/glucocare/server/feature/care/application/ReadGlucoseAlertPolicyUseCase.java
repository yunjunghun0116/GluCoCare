package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicy;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.dto.ReadGlucoseAlertPolicyResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadGlucoseAlertPolicyUseCase {
    private final MemberPatientRelationRepository memberPatientRelationRepository;
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    public ReadGlucoseAlertPolicyResponse execute(Long memberId, Long careGiverId) {
        var careGiverRelation = memberPatientRelationRepository.findById(careGiverId)
                                                               .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careGiverRelation.validateOwnership(memberId);
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findByMemberPatientRelation(careGiverRelation);
        return glucoseAlertPolicy.map(this::convertGlucoseAlertPolicyResponse)
                                 .orElseGet(() -> convertGlucoseAlertPolicyResponse(createGlucoseAlertPolicy(careGiverRelation)));
    }

    private ReadGlucoseAlertPolicyResponse convertGlucoseAlertPolicyResponse(GlucoseAlertPolicy glucoseAlertPolicy) {
        var careGiverRelation = glucoseAlertPolicy.getMemberPatientRelation();
        return ReadGlucoseAlertPolicyResponse.of(glucoseAlertPolicy.getId(), careGiverRelation.getId(), glucoseAlertPolicy.getHighRiskValue(), glucoseAlertPolicy.getVeryHighRiskValue());
    }

    private GlucoseAlertPolicy createGlucoseAlertPolicy(MemberPatientRelation memberPatientRelation) {
        var glucoseAlertPolicy = new GlucoseAlertPolicy(memberPatientRelation);
        return glucoseAlertPolicyRepository.save(glucoseAlertPolicy);
    }
}
