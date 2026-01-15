package com.glucocare.server.feature.auth.application;

import com.glucocare.server.feature.auth.dto.ExistsUniqueEmailRequest;
import com.glucocare.server.feature.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExistsUniqueEmailUseCase {
    private final MemberRepository memberRepository;

    public Boolean execute(ExistsUniqueEmailRequest request) {
        return memberRepository.existsByEmail(request.email());
    }
}
