package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.domain.RelationType;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.patient.dto.ReadPatientResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadPatientUseCase {
    private final MemberRepository memberRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    public ReadPatientResponse execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var relations = memberPatientRelationRepository.findByMemberAndRelationType(member, RelationType.PATIENT);
        if (relations.isEmpty()) {
            throw new ApplicationException(ErrorMessage.NOT_FOUND);
        }

        var relation = relations.getFirst();
        var patient = relation.getPatient();
        return ReadPatientResponse.of(patient.getId(), patient.getName(), patient.getCgmServerUrl());
    }
}
