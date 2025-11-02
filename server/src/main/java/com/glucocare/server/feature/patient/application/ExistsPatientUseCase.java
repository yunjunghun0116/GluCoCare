package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.domain.RelationType;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ExistsPatientUseCase {
    private final MemberRepository memberRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    public Boolean execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return memberPatientRelationRepository.existsByMemberAndRelationType(member, RelationType.PATIENT);
    }
}
