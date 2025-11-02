package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.dto.ReadCareGiverRelationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadCareGiverRelationUseCase {
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    public ReadCareGiverRelationResponse execute(Long memberId, Long id) {
        var careGiverRelation = memberPatientRelationRepository.findById(id)
                                                               .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careGiverRelation.validateOwnership(memberId);
        return convertCareGiverResponse(careGiverRelation);
    }

    private ReadCareGiverRelationResponse convertCareGiverResponse(MemberPatientRelation memberPatientRelation) {
        var patient = memberPatientRelation.getPatient();
        return ReadCareGiverRelationResponse.of(memberPatientRelation.getId(), patient.getId(), patient.getName());
    }
}
