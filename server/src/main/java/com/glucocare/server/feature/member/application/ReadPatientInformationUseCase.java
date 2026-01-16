package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.PatientInformationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadPatientInformationUseCase {
    private final MemberRepository memberRepository;

    public PatientInformationResponse execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return convertPatientInformationResponse(member);
    }

    private PatientInformationResponse convertPatientInformationResponse(Member member) {
        return PatientInformationResponse.of(member.getId(), member.getName(), member.getIsPatient(), member.getAccessCode());
    }
}
