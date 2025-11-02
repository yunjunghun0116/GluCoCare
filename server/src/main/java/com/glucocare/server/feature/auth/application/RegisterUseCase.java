package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.auth.domain.AuthToken;
import com.glucocare.server.feature.auth.domain.AuthTokenRepository;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.auth.dto.RegisterRequest;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenRepository authTokenRepository;

    public AuthResponse execute(RegisterRequest request) {
        var member = saveMemberWithRequest(request);
        return saveRefreshToken(member);
    }

    private void validateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ApplicationException(ErrorMessage.DUPLICATED_EMAIL);
        }
    }

    private Member saveMemberWithRequest(RegisterRequest request) {
        validateEmail(request.email());
        var encodedPassword = passwordEncoder.encode(request.password());
        var member = new Member(request.name(), request.email(), encodedPassword);
        return memberRepository.save(member);
    }

    private AuthResponse saveRefreshToken(Member member) {
        var tokenResponse = jwtProvider.generateToken(member);
        var authToken = new AuthToken(member, tokenResponse.refreshToken());
        authTokenRepository.save(authToken);
        return tokenResponse;
    }
}
