package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.domain.RelationType;
import com.glucocare.server.feature.care.dto.CreateCareGiverRelationRequest;
import com.glucocare.server.feature.care.dto.CreateCareGiverRelationResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCareGiverRelationUseCase {
    private final MemberRepository memberRepository;
    private final PatientRepository patientRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    public CreateCareGiverRelationResponse execute(Long memberId, CreateCareGiverRelationRequest request) {
        var careGiverRelation = saveCareGiverWithRequest(memberId, request);
        return createCareGiverRelationResponse(careGiverRelation);
    }

    private MemberPatientRelation saveCareGiverWithRequest(Long memberId, CreateCareGiverRelationRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var patient = patientRepository.findByNameAndId(request.name(), request.patientId())
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (memberPatientRelationRepository.existsByMemberAndPatient(member, patient)) {
            throw new ApplicationException(ErrorMessage.ALREADY_EXISTS);
        }
        var careGiverRelation = new MemberPatientRelation(member, patient, RelationType.CAREGIVER);
        return memberPatientRelationRepository.save(careGiverRelation);
    }

    private CreateCareGiverRelationResponse createCareGiverRelationResponse(MemberPatientRelation memberPatientRelation) {
        var patient = memberPatientRelation.getPatient();
        return CreateCareGiverRelationResponse.of(memberPatientRelation.getId(), patient.getId(), patient.getName());
    }
}
