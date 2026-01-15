package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.care.dto.CreateCareRelationRequest;
import com.glucocare.server.feature.care.dto.CreateCareRelationResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCareRelationUseCase {
    private final MemberRepository memberRepository;
    private final CareRelationRepository careRelationRepository;

    public CreateCareRelationResponse execute(Long memberId, CreateCareRelationRequest request) {
        var careRelation = saveCareRelationWithRequest(memberId, request);
        return createCareRelationResponse(careRelation);
    }

    private CareRelation saveCareRelationWithRequest(Long memberId, CreateCareRelationRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var patient = memberRepository.findById(request.patientId())
                                      .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!patient.getIsPatient()) {
            throw new ApplicationException(ErrorMessage.BAD_REQUEST);
        }
        if (careRelationRepository.existsByMemberAndPatient(member, patient)) {
            throw new ApplicationException(ErrorMessage.ALREADY_EXISTS);
        }
        var careRelation = new CareRelation(member, patient, request.relationType());
        return careRelationRepository.save(careRelation);
    }

    private CreateCareRelationResponse createCareRelationResponse(CareRelation careRelation) {
        var patient = careRelation.getPatient();
        return CreateCareRelationResponse.of(careRelation.getId(), patient.getId(), patient.getName());
    }
}
