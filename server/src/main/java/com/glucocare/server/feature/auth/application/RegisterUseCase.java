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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.ZoneOffset;
import java.util.Base64;

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
        var accessCode = generateAccessCode(member);
        member.updateAccessCode(accessCode);
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

    private String generateAccessCode(Member member) {
        try {
            var input = member.getId() + ":" + member.getCreatedAt()
                                                     .toInstant(ZoneOffset.UTC)
                                                     .getEpochSecond();
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(input.getBytes());

            var encoded = Base64.getUrlEncoder()
                                .withoutPadding()
                                .encodeToString(hash);
            return encoded.substring(0, 8);
        } catch (Exception e) {
            throw new ApplicationException(ErrorMessage.GENERATE_ACCESS_CODE_ERROR);
        }
    }
}
