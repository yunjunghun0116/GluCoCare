package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelation;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import com.glucocare.server.feature.care.dto.ReadCareRelationResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllCareRelationUseCase {
    private final MemberRepository memberRepository;
    private final CareRelationRepository careRelationRepository;

    public List<ReadCareRelationResponse> execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var careRelations = careRelationRepository.findAllByMember(member);
        return careRelations.stream()
                            .map((this::convertCareRelationResponse))
                            .toList();
    }

    private ReadCareRelationResponse convertCareRelationResponse(CareRelation careRelation) {
        var patient = careRelation.getPatient();
        return ReadCareRelationResponse.of(careRelation.getId(), patient.getId(), patient.getName());
    }
}
