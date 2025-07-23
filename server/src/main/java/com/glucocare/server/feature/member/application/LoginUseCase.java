package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.AuthResponse;
import com.glucocare.server.feature.member.dto.LoginRequest;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse execute(LoginRequest request) {
        var member = readMemberWithLoginRequest(request);
        return AuthResponse.of(jwtProvider.generateToken(member));
    }

    private Member readMemberWithLoginRequest(LoginRequest request) {
        var member = memberRepository.findByEmail(request.email())
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES);
        }
        return member;
    }
}
