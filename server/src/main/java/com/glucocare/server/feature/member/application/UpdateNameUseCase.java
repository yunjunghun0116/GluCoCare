package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.UpdateMemberNameRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNameUseCase {
    private final MemberRepository memberRepository;

    public void execute(Long memberId, UpdateMemberNameRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        member.updateName(request.name());
        memberRepository.save(member);
    }
}
