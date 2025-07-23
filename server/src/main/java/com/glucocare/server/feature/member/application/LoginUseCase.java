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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse execute(LoginRequest request) {
        var member = saveMemberWithMemberRequest(request);
        return createAuthResponseWithMember(member);
    }

    private AuthResponse createAuthResponseWithMember(Member member) {
        return AuthResponse.of(jwtProvider.generateToken(member));
    }

    private void validatePassword(LoginRequest request) {
        var encodedPassword = passwordEncoder.encode(request.password());
        if (memberRepository.existsByEmailAndPassword(request.email(), encodedPassword)) {
            throw new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES);
        }
    }

    private Member saveMemberWithMemberRequest(LoginRequest loginRequest) {
        validatePassword(loginRequest);
        return memberRepository.findByEmailAndPassword(loginRequest.email(), loginRequest.password())
                               .orElseThrow(() -> new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES));
    }
}
