package com.glucocare.server.feature.member.application;

import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.ExistsUniqueEmailRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ExistsUniqueEmailUseCase {
    private final MemberRepository memberRepository;

    public Boolean execute(ExistsUniqueEmailRequest request) {
        return memberRepository.existsByEmail(request.email());
    }
}
