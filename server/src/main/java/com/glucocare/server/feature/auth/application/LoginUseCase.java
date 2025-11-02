package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.auth.domain.AuthToken;
import com.glucocare.server.feature.auth.domain.AuthTokenRepository;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.auth.dto.LoginRequest;
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
public class LoginUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenRepository authTokenRepository;

    public AuthResponse execute(LoginRequest request) {
        var member = readMemberWithLoginRequest(request);
        return saveRefreshToken(member);
    }

    private Member readMemberWithLoginRequest(LoginRequest request) {
        var member = memberRepository.findByEmail(request.email())
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES);
        }
        return member;
    }

    private AuthResponse saveRefreshToken(Member member) {
        var tokenResponse = jwtProvider.generateToken(member);
        var authToken = authTokenRepository.findByMember(member);
        if (authToken.isEmpty()) {
            var newToken = new AuthToken(member, tokenResponse.refreshToken());
            authTokenRepository.save(newToken);
            return tokenResponse;
        }
        var savedAuthToken = authToken.get();
        savedAuthToken.updateRefreshToken(tokenResponse.refreshToken());
        return tokenResponse;
    }
}
