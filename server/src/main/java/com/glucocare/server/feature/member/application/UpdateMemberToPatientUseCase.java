package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.care.domain.RelationType;
import com.glucocare.server.feature.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMemberToPatientUseCase {
    private final MemberRepository memberRepository;
    private final CareRelationRepository careRelationRepository;

    public void execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        member.updateMemberToPatient();
        if (!careRelationRepository.existsByMemberAndPatientAndRelationType(member, member, RelationType.SELF)) {
            var careRelation = new CareRelation(member, member, RelationType.SELF);
            careRelationRepository.save(careRelation);
        }
    }
}
