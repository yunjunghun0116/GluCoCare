package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.auth.domain.AuthTokenRepository;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerateTokenUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final AuthTokenRepository authTokenRepository;

    public AuthResponse execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return jwtProvider.generateToken(member);
    }


}
