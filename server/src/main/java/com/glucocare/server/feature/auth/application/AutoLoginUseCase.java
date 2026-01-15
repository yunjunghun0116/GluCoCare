package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoLoginUseCase {
    private final MemberRepository memberRepository;

    public void execute(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApplicationException(ErrorMessage.BAD_REQUEST);
        }
    }
}
