package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.care.dto.ReadCareRelationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadCareRelationUseCase {
    private final CareRelationRepository careRelationRepository;

    public ReadCareRelationResponse execute(Long memberId, Long id) {
        var careRelation = careRelationRepository.findById(id)
                                                 .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careRelation.validateOwnership(memberId);
        return convertCareRelationResponse(careRelation);
    }

    private ReadCareRelationResponse convertCareRelationResponse(CareRelation careRelation) {
        var patient = careRelation.getPatient();
        return ReadCareRelationResponse.of(careRelation.getId(), patient.getId(), patient.getName());
    }
}
