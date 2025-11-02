package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.dto.ReadCareGiverRelationResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllCareGiverRelationUseCase {
    private final MemberRepository memberRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    public List<ReadCareGiverRelationResponse> execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var careGiverRelations = memberPatientRelationRepository.findAllByMember(member);
        return careGiverRelations.stream()
                                 .map((this::convertCareGiverRelationResponse))
                                 .toList();
    }

    private ReadCareGiverRelationResponse convertCareGiverRelationResponse(MemberPatientRelation memberPatientRelation) {
        var patient = memberPatientRelation.getPatient();
        return ReadCareGiverRelationResponse.of(memberPatientRelation.getId(), patient.getId(), patient.getName());
    }
}
