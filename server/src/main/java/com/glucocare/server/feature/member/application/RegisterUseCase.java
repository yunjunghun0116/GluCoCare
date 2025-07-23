package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.AuthResponse;
import com.glucocare.server.feature.member.dto.RegisterRequest;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse execute(RegisterRequest request) {
        var member = saveMemberWithMemberRequest(request);
        return AuthResponse.of(jwtProvider.generateToken(member));
    }

    private void validateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ApplicationException(ErrorMessage.DUPLICATED_EMAIL);
        }
    }

    private Member saveMemberWithMemberRequest(RegisterRequest request) {
        validateEmail(request.email());
        var encodedPassword = passwordEncoder.encode(request.password());
        var member = new Member(request.name(), request.email(), encodedPassword);
        return memberRepository.save(member);
    }
}
